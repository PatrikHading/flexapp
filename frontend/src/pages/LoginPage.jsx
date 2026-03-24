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
            navigate("/dashboard");
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="login-page">
            <div className="login-layout">
                <section className="login-brand-panel">
                    <div className="login-brand-content">
                        <div className="login-brand-badge">FlexApp</div>
                        <h1 className="login-brand-title">
                            Arbetstid, schema och historik på ett ställe.
                        </h1>
                        <p className="login-brand-text">
                            Ett internt verktyg för att registrera arbetstid, följa schema och
                            hantera din profil på ett tydligt och enkelt sätt.
                        </p>
                    </div>
                </section>

                <section className="login-form-panel">
                    <div className="login-card">
                        <h2 className="login-card-title">Logga in</h2>
                        <p className="login-card-subtitle">
                            Logga in med din e-post och ditt lösenord.
                        </p>

                        <form onSubmit={handleLogin} className="login-form">
                            <div className="login-field">
                                <label className="login-label">E-post</label>
                                <input
                                    className="app-input"
                                    type="email"
                                    placeholder="namn@exempel.se"
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                    required
                                />
                            </div>

                            <div className="login-field">
                                <label className="login-label">Lösenord</label>
                                <input
                                    className="app-input"
                                    type="password"
                                    placeholder="Ditt lösenord"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    required
                                />
                            </div>

                            <button className="app-button login-button" type="submit" disabled={loading}>
                                {loading ? "Loggar in..." : "Logga in"}
                            </button>
                        </form>

                        {error && <p className="app-message-error">{error}</p>}
                    </div>
                </section>
            </div>
        </div>
    );
}

export default LoginPage;