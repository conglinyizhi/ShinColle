#!/usr/bin/env sh
set -eu

CHANNEL="${1:-}"
CURRENT_TAG="${2:-}"
RELEASE_NAME="${3:-}"
MANUAL_NOTE="${4:-}"
OUTPUT_FILE="${5:-}"

if [ -z "$CHANNEL" ] || [ -z "$CURRENT_TAG" ] || [ -z "$RELEASE_NAME" ]; then
  echo "usage: $0 <channel> <current-tag> <release-name> [manual-note] [output-file]" >&2
  exit 1
fi

git fetch --tags --force >/dev/null 2>&1 || true

TMP_DIR="$(mktemp -d)"
cleanup() {
  rm -rf "$TMP_DIR"
}
trap cleanup EXIT INT TERM

for bucket in features fixes performance docs others; do
  : >"$TMP_DIR/$bucket"
done

collect_commits() {
  if git rev-parse -q --verify "refs/tags/$CURRENT_TAG" >/dev/null; then
    git log --no-merges --pretty='%s (%h)' "refs/tags/$CURRENT_TAG..HEAD"
  else
    git log --no-merges --pretty='%s (%h)' HEAD
  fi
}

append_commit() {
  bucket="$1"
  subject="$2"
  printf '%s\n' "- $subject" >>"$TMP_DIR/$bucket"
}

categorize_commits() {
  collect_commits | while IFS= read -r subject; do
    [ -n "$subject" ] || continue
    lower_subject="$(printf '%s' "$subject" | tr '[:upper:]' '[:lower:]')"
    case "$lower_subject" in
      feat:*|feat\(*)
        append_commit features "$subject"
        ;;
      fix:*|fix\(*|revert\ *)
        append_commit fixes "$subject"
        ;;
      perf:*|perf\(*)
        append_commit performance "$subject"
        ;;
      docs:*|docs\(*)
        append_commit docs "$subject"
        ;;
      *)
        append_commit others "$subject"
        ;;
    esac
  done
}

print_section() {
  title="$1"
  bucket="$2"
  if [ -s "$TMP_DIR/$bucket" ]; then
    printf '### %s\n\n' "$title"
    cat "$TMP_DIR/$bucket"
    printf '\n'
  fi
}

print_warning() {
  if [ "$CHANNEL" = "nightly" ]; then
    printf '警告：这是每夜预发布版本，仅供预览与回归验证，不建议用于正式存档。\n'
    printf 'Warning: This is a nightly prerelease for preview and regression validation only and is not recommended for production worlds.\n'
    printf '警告：これは nightly のプレリリースであり、プレビューと回帰確認専用です。本番ワールドでの利用は推奨されません。\n\n'
  else
    printf '警告：这是 Canary 测试版本，仅供测试，不建议用于正式存档。\n'
    printf 'Warning: This is a canary testing build for validation only and is not recommended for production worlds.\n'
    printf '警告：これはテスト専用のカナリア版であり、本番ワールドでの利用は推奨されません。\n\n'
  fi
}

print_shared_advice() {
  printf '%s\n' '- 虽然可以，但请不要将当前版本打包为整合包组件，我们尚未决定保证向后兼容 / Although it is allowed, please do not package this version as a modpack component; we have not yet decided to ensure backward compatibility. / 可能ですが、このバージョンを modpack コンポーネントとしてバンドルしないでください。後方互換性はまだ保証されていません。'
  printf '%s\n' '- 建议优先在单人环境中测试 / Single-player testing is recommended first / まずはシングルプレイ環境でのテストを推奨します。'
  printf '%s\n\n' '- 反馈问题时请附上 `latest.log` 或崩溃报告 / Please attach `latest.log` or crash reports when reporting issues / 問題を報告する際は `latest.log` またはクラッシュレポートを添付してください。'
}

print_manual_note() {
  [ -n "$MANUAL_NOTE" ] || return 0
  printf '<details>\n'
  printf '<summary>Manual Note / 构建备注 / 手動メモ</summary>\n\n'
  printf '## Reason / 原因 / 理由\n\n'
  printf '%s\n\n' "$MANUAL_NOTE"
  printf '</details>\n\n'
}

write_notes() {
  categorize_commits

  print_warning
  print_shared_advice
  print_manual_note

  if [ "$CHANNEL" = "nightly" ]; then
    printf '本次每夜版本变更 / Changes in this nightly / 今回の nightly 変更内容\n'
  else
    printf '本次金丝雀版本变更 / Changes in this canary / 今回のカナリア版の変更点\n'
  fi
  printf '以下为上次同渠道构建到本次构建之间的提交详情，仅提供中文。/ The details below cover commits between the previous build of the same channel and this build, in Chinese only. / 以下は同一チャネルの前回ビルドから今回ビルドまでのコミット詳細で、中国語のみです。\n\n'

  printf '<details>\n'
  printf '<summary>Commit Summary / 提交摘要 / コミット要約</summary>\n\n'

  printf '## %s\n\n' "$RELEASE_NAME"

  print_section '新特性' features
  print_section 'Bug 修复' fixes
  print_section '性能优化' performance
  print_section '文档调整' docs
  print_section '其他' others

  if [ ! -s "$TMP_DIR/features" ] && [ ! -s "$TMP_DIR/fixes" ] && [ ! -s "$TMP_DIR/performance" ] && [ ! -s "$TMP_DIR/docs" ] && [ ! -s "$TMP_DIR/others" ]; then
    printf '### 无新增提交\n\n'
    printf '- 当前范围内没有可汇总的提交。\n\n'
  fi

  printf '</details>\n'
}

if [ -n "$OUTPUT_FILE" ]; then
  write_notes >"$OUTPUT_FILE"
else
  write_notes
fi
