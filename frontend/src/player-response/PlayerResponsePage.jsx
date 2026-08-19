import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

export default function PlayerResponsePage() {
    const { token, response } =
        useParams();

    const [state, setState] =
        useState({
            loading: true,
            message: "",
            error: ""
        });

    useEffect(() => {
        async function submitResponse() {
            try {
                const result =
                    await fetch(
                        `/api/public/player-response/${token}/${response}`
                    );

                const body =
                    await result.json();

                if (!result.ok) {
                    throw new Error(
                        body.message ||
                        "Unable to process response."
                    );
                }

                setState({
                    loading: false,
                    message:
                        body.message ||
                        "Your response was recorded.",
                    error: ""
                });

            } catch (error) {
                setState({
                    loading: false,
                    message: "",
                    error:
                        error.message ||
                        "This response link is invalid or expired."
                });
            }
        }

        submitResponse();
    }, [token, response]);

    return (
        <main
            style={{
                minHeight: "100vh",
                display: "grid",
                placeItems: "center",
                padding: 24,
                background:
                    "linear-gradient(135deg,#0f172a,#273b86)"
            }}
        >
            <section
                style={{
                    width: "min(560px,100%)",
                    padding: 40,
                    borderRadius: 24,
                    background: "#ffffff",
                    textAlign: "center",
                    boxShadow:
                        "0 24px 70px rgba(0,0,0,.25)"
                }}
            >
                <div
                    style={{
                        fontSize: 30,
                        fontWeight: 900,
                        color: "#172033"
                    }}
                >
                    AthletiQ
                </div>

                {state.loading && (
                    <p>Processing your response...</p>
                )}

                {!state.loading && state.message && (
                    <>
                        <div
                            style={{
                                marginTop: 28,
                                fontSize: 48
                            }}
                        >
                            ✓
                        </div>

                        <h1>
                            Response recorded
                        </h1>

                        <p>{state.message}</p>
                    </>
                )}

                {!state.loading && state.error && (
                    <>
                        <div
                            style={{
                                marginTop: 28,
                                fontSize: 48
                            }}
                        >
                            !
                        </div>

                        <h1>
                            Unable to process response
                        </h1>

                        <p>{state.error}</p>
                    </>
                )}
            </section>
        </main>
    );
}