#!/usr/bin/env sh
set -eu

CURRENT_TAG="${1:-}"
OUTPUT_FILE="${2:-}"

if [ -z "$CURRENT_TAG" ]; then
  echo "usage: $0 <current-tag> [output-file]" >&2
  exit 1
fi

git fetch --tags --force >/dev/null 2>&1 || true

PREV_TAG="$(git tag --sort=-creatordate | grep '^canary-' | grep -Fxv "$CURRENT_TAG" | head -n 1 || true)"
if [ -z "$PREV_TAG" ]; then
  PREV_TAG="$(git tag --sort=-creatordate | grep -Fxv "$CURRENT_TAG" | head -n 1 || true)"
fi

TMP_DIR="$(mktemp -d)"
cleanup() {
  rm -rf "$TMP_DIR"
}
trap cleanup EXIT INT TERM

for bucket in features fixes performance docs others; do
  : >"$TMP_DIR/$bucket"
done

collect_commits() {
  if [ -n "$PREV_TAG" ]; then
    git log --no-merges --pretty='%s (%h)' "$PREV_TAG..$CURRENT_TAG"
  else
    git log --no-merges --pretty='%s (%h)' "$CURRENT_TAG"
  fi
}

append_commit() {
  bucket="$1"
  subject="$2"
  printf '%s\n' "- $subject" >>"$TMP_DIR/$bucket"
}

categorize_commits() {
  collect_commits | while IFS= read -r subject; do
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
    printf '## %s\n\n' "$title"
    cat "$TMP_DIR/$bucket"
    printf '\n'
  fi
}

write_notes() {
  categorize_commits

  printf '警告：这是 Canary 测试版本，仅供测试，不建议用于正式存档。\n'
  printf 'Warning: This is a canary testing build for validation only and is not recommended for production worlds.\n'
  printf '警告：これはテスト専用のカナリア版であり、本番ワールドでの利用は推奨されません。\n\n'

  printf '%s\n' '- 虽然可以，但请不要将当前版本打包为整合包组件，我们尚未决定保证向后兼容 / Although it is allowed, please do not package this version as a modpack component; we have not yet decided to ensure backward compatibility. / 可能ですが、このバージョンを modpack コンポーネントとしてバンドルしないでください。後方互換性はまだ保証されていません。'
  printf '%s\n' '- 建议优先在单人环境中测试 / Single-player testing is recommended first / まずはシングルプレイ環境でのテストを推奨します。'
  printf '%s\n\n' '- 反馈问题时请附上 `latest.log` 或崩溃报告 / Please attach `latest.log` or crash reports when reporting issues / 問題を報告する際は `latest.log` またはクラッシュレポートを添付してください。'

  printf '本次金丝雀版本变更 / Changes in this canary / 今回のカナリア版の変更点\n'
  printf '以下提交摘要仅提供中文。/ Chinese only below. / 以下のコミット要約は中国語のみです。\n'
  printf '<details>\n'
  printf '<summary>展开提交摘要 / Expand commit summary / コミット要約を展開</summary>\n\n'

  print_section '新特性' features
  print_section 'Bug 修复' fixes
  print_section '性能优化' performance
  print_section '文档调整' docs
  print_section '其他' others

  printf '</details>\n'
}

if [ -n "$OUTPUT_FILE" ]; then
  write_notes >"$OUTPUT_FILE"
else
  write_notes
fi
