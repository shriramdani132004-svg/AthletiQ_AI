from pathlib import Path
import sys

ROOT = Path(r"C:\Users\SHRIRAM\Desktop\AthletiQ_AI")
FRONTEND = ROOT / "frontend"

JSX = FRONTEND / "src" / "applications" / "EventApplicationsPage.jsx"
CSS = FRONTEND / "src" / "applications" / "EventApplicationsPage.css"

page = JSX.read_text(encoding="utf-8").splitlines()
css_text = CSS.read_text(encoding="utf-8")

def fail(message):
    raise RuntimeError(message)

# ============================================================
# STEP 1: Replace current 11.6 AI state
# ============================================================

state_start = next(
    (
        i for i, line in enumerate(page)
        if "const [aiEvaluatingApplicationId," in line
    ),
    -1
)

if state_start == -1:
    fail("aiEvaluatingApplicationId state not found.")

state_end = next(
    (
        i for i in range(state_start, min(len(page), state_start + 20))
        if "useState(new Set());" in page[i]
    ),
    -1
)

if state_end == -1:
    fail("11.6 AI evaluated-ID state ending not found.")

new_state = [
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

page = page[:state_start] + new_state + page[state_end + 1:]

print("AI result state        : PASS")

# ============================================================
# STEP 2: Replace evaluateAI handler
# ============================================================

handler_start = next(
    (
        i for i, line in enumerate(page)
        if "async function evaluateAI" in line
    ),
    -1
)

if handler_start == -1:
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

if handler_end == -1:
    fail("evaluateAI handler boundary not found.")

new_handler = [
    "    async function evaluateAI(",
    "        applicationId",
    "    ){",
    "",
    "        if(!user?.userId){",
    "",
    "            setError(",
    '                "Organizer authentication is required."',
    "            );",
    "",
    "            return;",
    "        }",
    "",
    "        setAiEvaluatingApplicationId(",
    "            applicationId",
    "        );",
    "",
    "        try{",
    "",
    "            const result =",
    "                await applicationApi.evaluateAI(",
    "                    eventId,",
    "                    applicationId,",
    "                    user.userId",
    "                );",
    "",
    "            setAiResults(",
    "                current => ({",
    "                    ...current,",
    "                    [applicationId]: result",
    "                })",
    "            );",
    "",
    "            setSelectedAiApplicationId(",
    "                applicationId",
    "            );",
    "",
    "        }catch(err){",
    "",
    "            setError(",
    "                err.message ||",
    '                "Unable to evaluate candidate with AI."',
    "            );",
    "",
    "        }finally{",
    "",
    "            setAiEvaluatingApplicationId(",
    "                null",
    "            );",
    "        }",
    "    }",
]

page = page[:handler_start] + new_handler + page[handler_end + 1:]

print("AI result retention    : PASS")

# ============================================================
# STEP 3: Replace actual AI button by semantic line location
# ============================================================

button_start = next(
    (
        i for i, line in enumerate(page)
        if 'className="application-ai-button"' in line
    ),
    -1
)

if button_start == -1:
    fail("AI button not found.")

button_end = next(
    (
        i for i in range(button_start, min(len(page), button_start + 40))
        if page[i].strip() == "</button>"
    ),
    -1
)

if button_end == -1:
    fail("AI button closing tag not found.")

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
    "                                                        if(",
    "                                                            aiResults[",
    "                                                                application.applicationId",
    "                                                            ]",
    "                                                        ){",
    "",
    "                                                            setSelectedAiApplicationId(",
    "                                                                application.applicationId",
    "                                                            );",
    "",
    "                                                            openApplication(",
    "                                                                application.applicationId",
    "                                                            );",
    "",
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
    "                                                            : aiResults[",
    "                                                                application.applicationId",
    "                                                            ]",
    '                                                                ? "AI Report"',
    '                                                                : "AI Evaluate"',
    "                                                    }",
    "                                                </button>",
]

page = page[:button_start] + new_button + page[button_end + 1:]

print(
    f"AI Report button       : PASS "
    f"(replaced lines {button_start + 1}-{button_end + 1})"
)

# ============================================================
# STEP 4: Add safe object formatter
# ============================================================

formatter = [
    "function formatAiReportItem(item){",
    "",
    "    if(item === null || item === undefined){",
    '        return "—";',
    "    }",
    "",
    '    if(typeof item === "object"){',
    "",
    "        const requirement =",
    "            item.requirement ??",
    "            item.name ??",
    "            item.title ??",
    "            item.label ??",
    '            "";',
    "",
    "        const status =",
    "            item.status ??",
    "            item.result ??",
    "            item.assessment ??",
    '            "";',
    "",
    "        const evidence =",
    "            item.evidence ??",
    "            item.explanation ??",
    "            item.reason ??",
    '            "";',
    "",
    "        const parts =",
    "            [requirement,status,evidence]",
    "                .filter(",
    "                    value =>",
    "                        value !== null &&",
    "                        value !== undefined &&",
    '                        String(value).trim() !== ""',
    "                )",
    "                .map(value => String(value));",
    "",
    "        if(parts.length > 0){",
    '            return parts.join(" • ");',
    "        }",
    "",
    "        try{",
    "            return JSON.stringify(item);",
    "        }catch{",
    "            return String(item);",
    "        }",
    "    }",
    "",
    "    return String(item);",
    "}",
    "",
]

if not any("function formatAiReportItem" in x for x in page):

    export_index = next(
        (
            i for i, line in enumerate(page)
            if "export default function EventApplicationsPage()" in line
        ),
        -1
    )

    if export_index == -1:
        fail("EventApplicationsPage export not found.")

    page = page[:export_index] + formatter + page[export_index:]

print("AI object formatter    : PASS")

# ============================================================
# STEP 5: Insert report before Submitted Answers
# ============================================================

submitted_index = next(
    (
        i for i, line in enumerate(page)
        if line.strip() == "Submitted Answers"
    ),
    -1
)

if submitted_index == -1:
    fail("Submitted Answers heading not found.")

section_start = -1

for i in range(submitted_index, -1, -1):

    if page[i].strip() == "<section>":
        section_start = i
        break

if section_start == -1:
    fail("Submitted Answers section wrapper not found.")

report = [
    "                                {selectedAiApplicationId &&",
    "                                    aiResults[selectedAiApplicationId] && (",
    "",
    '                                    <section className="application-ai-report">',
    "",
    '                                        <div className="application-ai-report-header">',
    "",
    "                                            <div>",
    "                                                <span>",
    "                                                    PHASE 11 AI",
    "                                                </span>",
    "                                                <h3>",
    "                                                    AI Candidate Assessment",
    "                                                </h3>",
    "                                            </div>",
    "",
    '                                            <div className="application-ai-report-score">',
    "                                                <span>",
    "                                                    AI Score",
    "                                                </span>",
    "                                                <strong>",
    "                                                    {",
    "                                                        aiResults[",
    "                                                            selectedAiApplicationId",
    '                                                        ].score ?? "—"',
    "                                                    }",
    "                                                </strong>",
    "                                            </div>",
    "",
    "                                        </div>",
    "",
    '                                        <div className="application-ai-report-recommendation">',
    "                                            <span>",
    "                                                Recommendation",
    "                                            </span>",
    "                                            <strong>",
    "                                                {",
    "                                                    aiResults[",
    "                                                        selectedAiApplicationId",
    "                                                    ].recommendation ||",
    '                                                    "No recommendation"',
    "                                                }",
    "                                            </strong>",
    "                                        </div>",
    "",
    '                                        <div className="application-ai-report-section">',
    "                                            <span>",
    "                                                Assessment",
    "                                            </span>",
    "                                            <p>",
    "                                                {",
    "                                                    aiResults[",
    "                                                        selectedAiApplicationId",
    "                                                    ].assessment ||",
    '                                                    "No assessment provided."',
    "                                                }",
    "                                            </p>",
    "                                        </div>",
    "",
    '                                        <div className="application-ai-report-grid">',
    "",
    '                                            <div className="application-ai-report-section">',
    "                                                <span>",
    "                                                    Strengths",
    "                                                </span>",
    "                                                <ul>",
    "                                                    {",
    "                                                        Array.isArray(",
    "                                                            aiResults[",
    "                                                                selectedAiApplicationId",
    "                                                            ].strengths",
    "                                                        ) &&",
    "                                                        aiResults[",
    "                                                            selectedAiApplicationId",
    "                                                        ].strengths.map(",
    "                                                            (item,index) => (",
    "                                                                <li key={index}>",
    "                                                                    {formatAiReportItem(item)}",
    "                                                                </li>",
    "                                                            )",
    "                                                        )",
    "                                                    }",
    "                                                </ul>",
    "                                            </div>",
    "",
    '                                            <div className="application-ai-report-section">',
    "                                                <span>",
    "                                                    Weaknesses",
    "                                                </span>",
    "                                                <ul>",
    "                                                    {",
    "                                                        Array.isArray(",
    "                                                            aiResults[",
    "                                                                selectedAiApplicationId",
    "                                                            ].weaknesses",
    "                                                        ) &&",
    "                                                        aiResults[",
    "                                                            selectedAiApplicationId",
    "                                                        ].weaknesses.map(",
    "                                                            (item,index) => (",
    "                                                                <li key={index}>",
    "                                                                    {formatAiReportItem(item)}",
    "                                                                </li>",
    "                                                            )",
    "                                                        )",
    "                                                    }",
    "                                                </ul>",
    "                                            </div>",
    "",
    "                                        </div>",
    "",
    '                                        <div className="application-ai-report-section">',
    "                                            <span>",
    "                                                Experience Analysis",
    "                                            </span>",
    "                                            <p>",
    "                                                {",
    "                                                    aiResults[",
    "                                                        selectedAiApplicationId",
    "                                                    ].experienceAnalysis ||",
    '                                                    "No experience analysis provided."',
    "                                                }",
    "                                            </p>",
    "                                        </div>",
    "",
    '                                        <div className="application-ai-report-section">',
    "                                            <span>",
    "                                                Requirement Fit",
    "                                            </span>",
    "                                            <ul>",
    "                                                {",
    "                                                    Array.isArray(",
    "                                                        aiResults[",
    "                                                            selectedAiApplicationId",
    "                                                        ].requirementFit",
    "                                                    ) &&",
    "                                                    aiResults[",
    "                                                        selectedAiApplicationId",
    "                                                    ].requirementFit.map(",
    "                                                        (item,index) => (",
    "                                                            <li key={index}>",
    "                                                                {formatAiReportItem(item)}",
    "                                                            </li>",
    "                                                        )",
    "                                                    )",
    "                                                }",
    "                                            </ul>",
    "                                        </div>",
    "",
    '                                        <div className="application-ai-report-section">',
    "                                            <span>",
    "                                                Position Suitability",
    "                                            </span>",
    "                                            <p>",
    "                                                {",
    "                                                    aiResults[",
    "                                                        selectedAiApplicationId",
    "                                                    ].positionSuitability ||",
    '                                                    "No position assessment provided."',
    "                                                }",
    "                                            </p>",
    "                                        </div>",
    "",
    '                                        <div className="application-ai-report-section">',
    "                                            <span>",
    "                                                Concerns",
    "                                            </span>",
    "                                            <ul>",
    "                                                {",
    "                                                    Array.isArray(",
    "                                                        aiResults[",
    "                                                            selectedAiApplicationId",
    "                                                        ].concerns",
    "                                                    ) &&",
    "                                                    aiResults[",
    "                                                        selectedAiApplicationId",
    "                                                    ].concerns.map(",
    "                                                        (item,index) => (",
    "                                                            <li key={index}>",
    "                                                                {formatAiReportItem(item)}",
    "                                                            </li>",
    "                                                        )",
    "                                                    )",
    "                                                }",
    "                                            </ul>",
    "                                        </div>",
    "",
    '                                        <div className="application-ai-report-section">',
    "                                            <span>",
    "                                                Explanation",
    "                                            </span>",
    "                                            <p>",
    "                                                {",
    "                                                    aiResults[",
    "                                                        selectedAiApplicationId",
    "                                                    ].explanation ||",
    '                                                    "No explanation provided."',
    "                                                }",
    "                                            </p>",
    "                                        </div>",
    "",
    '                                        <p className="application-ai-advisory">',
    "                                            AI recommendations are advisory only.",
    "                                            Final player selection remains with the organizer.",
    "                                        </p>",
    "",
    "                                    </section>",
    "",
    "                                )}",
    "",
]

page = page[:section_start] + report + page[section_start:]

print("AI report JSX         : PASS")

# ============================================================
# STEP 6: CSS
# ============================================================

if ".application-ai-report" not in css_text:

    css_text += r'''

/* ============================================================
   PHASE 11.7 — AI CANDIDATE REPORT
   ============================================================*/

.application-ai-report {

    margin:
        18px 0 20px;

    padding:
        18px;

    border:
        1px solid
        rgba(129,140,248,.18);

    border-radius:
        15px;

    background:
        linear-gradient(
            145deg,
            rgba(79,70,229,.07),
            rgba(15,23,42,.60)
        );

    box-shadow:
        inset
        0 1px 0
        rgba(255,255,255,.025);
}

.application-ai-report-header {

    display:
        flex;

    align-items:
        flex-start;

    justify-content:
        space-between;

    gap:
        16px;

    margin-bottom:
        14px;
}

.application-ai-report-header span,
.application-ai-report-section > span,
.application-ai-report-recommendation > span {

    display:
        block;

    color:
        #818cf8;

    font-size:
        7px;

    font-weight:
        950;

    letter-spacing:
        .10em;

    text-transform:
        uppercase;
}

.application-ai-report-header h3 {

    margin:
        5px 0 0;

    color:
        #eef2ff;

    font-size:
        18px;

    font-weight:
        950;
}

.application-ai-report-score {

    min-width:
        80px;

    padding:
        9px;

    text-align:
        center;

    border:
        1px solid
        rgba(129,140,248,.18);

    border-radius:
        10px;

    background:
        rgba(99,102,241,.07);
}

.application-ai-report-score strong {

    display:
        block;

    margin-top:
        3px;

    color:
        #c7d2fe;

    font-size:
        25px;

    font-weight:
        950;
}

.application-ai-report-recommendation {

    margin-bottom:
        13px;

    padding:
        10px 12px;

    border:
        1px solid
        rgba(129,140,248,.12);

    border-radius:
        10px;

    background:
        rgba(99,102,241,.045);
}

.application-ai-report-recommendation strong {

    display:
        block;

    margin-top:
        4px;

    color:
        #a5b4fc;

    font-size:
        11px;

    font-weight:
        950;
}

.application-ai-report-section {

    margin-top:
        13px;
}

.application-ai-report-section p {

    margin:
        5px 0 0;

    color:
        #94a3b8;

    font-size:
        9px;

    line-height:
        1.6;
}

.application-ai-report-section ul {

    display:
        grid;

    gap:
        5px;

    margin:
        6px 0 0;

    padding-left:
        17px;

    color:
        #cbd5e1;

    font-size:
        9px;

    line-height:
        1.5;
}

.application-ai-report-grid {

    display:
        grid;

    grid-template-columns:
        repeat(
            2,
            minmax(0,1fr)
        );

    gap:
        14px;
}

.application-ai-advisory {

    margin:
        16px 0 0;

    padding-top:
        10px;

    border-top:
        1px solid
        rgba(148,163,184,.08);

    color:
        #64748b;

    font-size:
        7px;

    line-height:
        1.5;
}

@media (max-width: 760px) {

    .application-ai-report-header {
        flex-direction: column;
    }

    .application-ai-report-score {
        width: 100%;
        box-sizing: border-box;
    }

    .application-ai-report-grid {
        grid-template-columns: 1fr;
    }
}
'''

print("AI report CSS         : PASS")

# ============================================================
# STEP 7: HARD VERIFICATION
# ============================================================

final_page = "\n".join(page)

required = [
    "aiResults",
    "selectedAiApplicationId",
    "setAiResults",
    "setSelectedAiApplicationId",
    '"AI Report"',
    "application-ai-report",
    "AI Candidate Assessment",
    "formatAiReportItem",
    "AI recommendations are advisory only",
]

for needle in required:
    if needle not in final_page:
        fail(f"HARD VERIFICATION FAILED: {needle}")

JSX.write_text(final_page + "\n", encoding="utf-8")
CSS.write_text(css_text, encoding="utf-8")

print("")
print("STEP 11.7 SOURCE PATCH: PASS")
