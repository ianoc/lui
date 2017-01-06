# we can get fancier later, see:
# https://robots.thoughtbot.com/use-git-hooks-to-automate-annoying-tasks
# for an example of only checking changed files
# add this to .git/hook/pre-commit and chmod +x

function checkscala() {
  errored=$(scripts/format_scala.sh -t 2>&1 | grep "ERROR")
  [ -z "$errored" ] && return 0

  for ferr in $errored; do
    echo >&2 "$ferr"
  done

  echo >&2 "Scala should be formatted with scripts/format_scala.sh"

  return 1
}

checkscala || fail=yes
[ -z "$fail" ] || exit 1

exit 0
