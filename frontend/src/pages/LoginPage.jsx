import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { loginUser } from "../services/auth";

function LoginPage({ setUser }) {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();
        setError("");
        setLoading(true);

        try {
            const loggedInUser = await loginUser(email, password);
            setUser(loggedInUser);
            navigate("/");
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={styles.page}>
            <div style={styles.card}>
                <h1>Logga in</h1>

                <form onSubmit={handleLogin} style={styles.form}>
                    <input
                        type="email"
                        placeholder="E-post"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        style={styles.input}
                        required
                    />

                    <input
                        type="password"
                        placeholder="Lösenord"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        style={styles.input}
                        required
                    />

                    <button type="submit" style={styles.button} disabled={loading}>
                        {loading ? "Loggar in..." : "Logga in"}
                    </button>
                </form>

                {error && <p style={styles.error}>{error}</p>}
            </div>
        </div>
    );
}

const styles = {
    page: {
        minHeight: "100vh",
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        padding: "20px",
    },
    card: {
        width: "100%",
        maxWidth: "420px",
        background: "#fff",
        padding: "24px",
        borderRadius: "12px",
        boxShadow: "0 8px 24px rgba(0,0,0,0.08)",
    },
    form: {
        display: "flex",
        flexDirection: "column",
        gap: "12px",
        marginTop: "16px",
    },
    input: {
        padding: "12px",
        fontSize: "16px",
        borderRadius: "8px",
        border: "1px solid #ccc",
    },
    button: {
        padding: "12px",
        fontSize: "16px",
        borderRadius: "8px",
        border: "none",
        cursor: "pointer",
        background: "#222",
        color: "#fff",
    },
    error: {
        color: "crimson",
        marginTop: "12px",
    },
};

export default LoginPage;