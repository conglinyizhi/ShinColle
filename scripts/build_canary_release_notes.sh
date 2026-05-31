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

write_notes() {
  printf '警告：这是金丝雀测试版本，仅供测试，不建议用于正式存档。\n'
  printf 'Warning: This is a canary testing build for validation only and is not recommended for production worlds.\n'
  printf '警告：これはテスト専用のカナリア版であり、本番ワールドでの利用は推奨されません。\n\n'

  printf '%s\n' '- 建议优先在单人环境中测试。'
  printf '%s\n' '- Single-player testing is recommended first.'
  printf '%s\n\n' '- まずはシングルプレイ環境でのテストを推奨します。'

  printf '%s\n' '- 反馈问题时请附上 latest.log 或崩溃报告。'
  printf '%s\n' '- Please attach latest.log or crash reports when reporting issues.'
  printf '%s\n\n' '- 問題を報告する際は latest.log またはクラッシュレポートを添付してください。'

  printf '本次金丝雀版本变更\n'
  printf 'Changes in this canary\n'
  printf '今回のカナリア版の変更点\n\n'

  printf '以下提交摘要仅提供中文。\n'
  printf 'Chinese only below.\n'
  printf '以下のコミット要約は中国語のみです。\n'
  printf '<details>\n'
  printf '<summary>展开提交摘要 / Expand commit summary / コミット要約を展開</summary>\n\n'
  printf '```text\n'
  if [ -n "$PREV_TAG" ]; then
    git log --no-merges --pretty='- %s (%h)' "$PREV_TAG..$CURRENT_TAG"
  else
    git log --no-merges --pretty='- %s (%h)' "$CURRENT_TAG"
  fi
  printf '```\n'
  printf '</details>\n'
}

if [ -n "$OUTPUT_FILE" ]; then
  write_notes >"$OUTPUT_FILE"
else
  write_notes
fi
