from pathlib import Path
import re
import sys

ROOT = Path(r"C:\Users\SHRIRAM\Desktop\AthletiQ_AI")
JSX = ROOT / "frontend" / "src" / "applications" / "EventApplicationsPage.jsx"
CSS = ROOT / "frontend" / "src" / "applications" / "EventApplicationsPage.css"

page = JSX.read_text(encoding="utf-8").splitlines()
css = CSS.read_text(encoding="utf-8")

def fail(msg):
    raise RuntimeError(msg)

# ============================================================
# STAGE 1
# state + handler + button
# ============================================================

# --- state ---
state_start = next(
    (i for i,x in enumerate(page)
     if "const [aiEvaluatingApplicationId," in x),
    -1
)

if state_start < 0:
    fail("11.6 AI state not found.")

state_end = next(
    (i for i in range(state_start, min(len(page), state_start+20))
     if "useState(new Set());" in page[i]),
    -1
)

if state_end < 0:
    fail("11.6 AI state ending not found.")

page = (
    page[:state_start]
    + [
        "    const [aiEvaluatingApplicationId,",
        "        setAiEvaluatingApplicationId] =",
        "        useState(null);",
        "",
        "    const [aiResults,",
        "        setAiResults] =",
        "        useState({});",
        "",
        "    const [selectedAiApplicationId,",
        "        setSelectedAiApplicationId] =",
        "        useState(null);",
    ]
    + page[state_end+1:]
)

print("STAGE 1 state: PASS")

# --- handler ---
handler_start = next(
    (i for i,x in enumerate(page)
     if "async function evaluateAI" in x),
    -1
)

if handler_start < 0:
    fail("evaluateAI handler not found.")

depth = 0
handler_end = -1
started = False

for i in range(handler_start, len(page)):
    depth += page[i].count("{")
    depth -= page[i].count("}")

    if "{" in page[i]:
        started = True

    if started and depth == 0:
        handler_end = i
        break

if handler_end < 0:
    fail("evaluateAI handler boundary not found.")

page = (
    page[:handler_start]
    + [
        "    async function evaluateAI(",
        "        applicationId",
        "    ){",
        "",
        "        if(!user?.userId){",
        "            setError(",
        '                "Organizer authentication is required."',
        "            );",
        "            return;",
        "        }",
        "",
        "        setAiEvaluatingApplicationId(applicationId);",
        "",
        "        try{",
        "            const result =",
        "                await applicationApi.evaluateAI(",
        "                    eventId,",
        "                    applicationId,",
        "                    user.userId",
        "                );",
        "",
        "            setAiResults(current => ({",
        "                ...current,",
        "                [applicationId]: result",
        "            }));",
        "",
        "            setSelectedAiApplicationId(applicationId);",
        "",
        "        }catch(err){",
        "            setError(",
        "                err.message ||",
        '                "Unable to evaluate candidate with AI."',
        "            );",
        "        }finally{",
        "            setAiEvaluatingApplicationId(null);",
        "        }",
        "    }",
        ""
    ]
    + page[handler_end+1:]
)

print("STAGE 1 handler: PASS")

# --- button ---
button_start = next(
    (i for i,x in enumerate(page)
     if 'className="application-ai-button"' in x),
    -1
)

if button_start < 0:
    fail("AI button not found.")

button_end = next(
    (i for i in range(button_start, min(len(page), button_start+35))
     if page[i].strip() == "</button>"),
    -1
)

if button_end < 0:
    fail("AI button end not found.")

new_button = [
    "                                                <button",
    '                                                    type="button"',
    '                                                    className="application-ai-button"',
    "                                                    disabled={",
    "                                                        aiEvaluatingApplicationId ===",
    "                                                        application.applicationId",
    "                                                    }",
    "                                                    onClick={() => {",
    "",
    "                                                        if (",
    "                                                            aiResults[application.applicationId]",
    "                                                        ) {",
    "                                                            setSelectedAiApplicationId(",
    "                                                                application.applicationId",
    "                                                            );",
    "                                                            openApplication(",
    "                                                                application.applicationId",
    "                                                            );",
    "                                                            return;",
    "                                                        }",
    "",
    "                                                        evaluateAI(",
    "                                                            application.applicationId",
    "                                                        );",
    "                                                    }}",
    "                                                >",
    "                                                    {",
    "                                                        aiEvaluatingApplicationId ===",
    "                                                        application.applicationId",
    '                                                            ? "Evaluating..."',
    "                                                            : aiResults[application.applicationId]",
    '                                                                ? "AI Report"',
    '                                                                : "AI Evaluate"',
    "                                                    }",
    "                                                </button>"
]

page = page[:button_start] + new_button + page[button_end+1:]

print("STAGE 1 button: PASS")

# --- formatter ---
if not any("function formatAiReportItem" in x for x in page):

    export_index = next(
        (i for i,x in enumerate(page)
         if "export default function EventApplicationsPage()" in x),
        -1
    )

    if export_index < 0:
        fail("Component export not found.")

    formatter = [
        "function formatAiReportItem(item){",
        "    if(item === null || item === undefined){",
        '        return "—";',
        "    }",
        '    if(typeof item === "object"){',
        "        const requirement =",
        "            item.requirement ??",
        "            item.name ??",
        "            item.title ??",
        "            item.label ??",
        '            "";',
        "        const status =",
        "            item.status ??",
        "            item.result ??",
        "            item.assessment ??",
        '            "";',
        "        const evidence =",
        "            item.evidence ??",
        "            item.explanation ??",
        "            item.reason ??",
        '            "";',
        "        const parts =",
        "            [requirement,status,evidence]",
        "                .filter(value =>",
        "                    value !== null &&",
        "                    value !== undefined &&",
        '                    String(value).trim() !== ""',
        "                )",
        "                .map(value => String(value));",
        "        if(parts.length > 0){",
        '            return parts.join(" • ");',
        "        }",
        "        try{",
        "            return JSON.stringify(item);",
        "        }catch{",
        "            return String(item);",
        "        }",
        "    }",
        "    return String(item);",
        "}",
        ""
    ]

    page = page[:export_index] + formatter + page[export_index:]

# ============================================================
# WRITE STAGE 1
# ============================================================

JSX.write_text("\n".join(page) + "\n", encoding="utf-8")

print("STAGE 1 source write: PASS")

# Hard stage-1 verification
verify = JSX.read_text(encoding="utf-8")

for needle in [
    "aiResults",
    "selectedAiApplicationId",
    "setAiResults",
    "setSelectedAiApplicationId",
    '"AI Report"',
    "application-ai-button"
]:
    if needle not in verify:
        fail(f"STAGE 1 verification failed: {needle}")

print("STAGE 1 verification: PASS")

# Return to PowerShell for build.
print("STAGE 1 COMPLETE")
