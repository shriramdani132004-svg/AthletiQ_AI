import { useEffect, useState } from "react";

function App() {
  const [backend, setBackend] = useState("Checking...");
  const [error, setError] = useState("");

  useEffect(() => {
    fetch("/api/v1/health")
      .then((response) => {
        if (!response.ok) {
          throw new Error(`HTTP ${response.status}`);
        }
        return response.json();
      })
      .then((data) => {
        setBackend(data.status === "UP" ? "CONNECTED" : data.status);
      })
      .catch((err) => {
        setBackend("DISCONNECTED");
        setError(err.message);
      });
  }, []);

  return (
    <main style={{ fontFamily: "Arial, sans-serif", padding: "40px" }}>
      <h1>AthletiQ</h1>
      <p>AI-powered sports talent recruitment platform</p>

      <section>
        <h2>System Status</h2>
        <p>
          Frontend: <strong>ONLINE</strong>
        </p>
        <p>
          Backend: <strong>{backend}</strong>
        </p>
        {error && <p>Error: {error}</p>}
      </section>
    </main>
  );
}

export default App;