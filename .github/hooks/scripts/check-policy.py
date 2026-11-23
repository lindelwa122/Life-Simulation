#!/usr/bin/env python3
import json
import re
import sys

try:
    event = json.load(sys.stdin)
except json.JSONDecodeError:
    sys.exit(0)

text = json.dumps(event).lower()

blocked_patterns = [
    r"rm\s+-rf\s+[^\"']*(target|output)(/|\s|\"|'|$)",
    r"git\s+reset\s+--hard",
    r"git\s+clean\s+-[^\n]*f",
]

for pattern in blocked_patterns:
    if re.search(pattern, text):
        print(json.dumps({
            "hookSpecificOutput": {
                "hookEventName": "PreToolUse",
                "permissionDecision": "deny",
                "permissionDecisionReason": "Blocked by project policy: destructive target/output or Git cleanup is not allowed."
            }
        }))
        sys.exit(2)

sys.exit(0)
