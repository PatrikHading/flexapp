import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { changePassword, clearAuthHeader } from "../services/auth";

function ProfilePage({ user, setUser }) {
    const [currentPassword, setCurrentPassword] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const [successMessage, setSuccessMessage] = useState("");

    const navigate = useNavigate();

    const handleChangePassword = async (e) => {
        e.preventDefault();
        setError("");
        setSuccessMessage("");

        if (newPassword !== confirmPassword) {
            setError("Det nya lösenordet och bekräftelsen matchar inte.");
            return;
        }

        if (newPassword.length < 6) {
            setError("Det nya lösenordet måste vara minst 6 tecken.");
            return;
        }

        try {
            setLoading(true);

            await changePassword(currentPassword, newPassword);

            setSuccessMessage("Lösenordet har uppdaterats. Du behöver logga in igen.");

            setCurrentPassword("");
            setNewPassword("");
            setConfirmPassword("");

            setTimeout(() => {
                clearAuthHeader();
                setUser(null);
                navigate("/login");
            }, 1500);
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="page-section">
            <section className="app-card-hero">
                <h1 className="app-card-title">Min profil</h1>
                <p className="app-card-subtitle">
                    Se dina användaruppgifter och hantera ditt konto.
                </p>
            </section>

            <section className="app-card">
                <h2>Profiluppgifter</h2>

                <div className="app-grid">
                    <div className="app-info-box">
                        <span className="app-label">Förnamn</span>
                        <span className="app-value">{user?.firstName || "-"}</span>
                    </div>

                    <div className="app-info-box">
                        <span className="app-label">Efternamn</span>
                        <span className="app-value">{user?.lastName || "-"}</span>
                    </div>

                    <div className="app-info-box">
                        <span className="app-label">E-post</span>
                        <span className="app-value">{user?.email || "-"}</span>
                    </div>

                    <div className="app-info-box">
                        <span className="app-label">Roll</span>
                        <span className="app-value">{user?.role || "-"}</span>
                    </div>

                    <div className="app-info-box">
                        <span className="app-label">Status</span>
                        <span className="app-value">
              {user?.active ? "Aktiv" : "Inaktiv"}
            </span>
                    </div>

                    <div className="app-info-box">
                        <span className="app-label">Användar-ID</span>
                        <span className="app-value">{user?.id || "-"}</span>
                    </div>
                </div>
            </section>

            <section className="app-card">
                <h2>Byt lösenord</h2>
                <p className="app-card-subtitle">
                    Ange ditt nuvarande lösenord och välj ett nytt.
                </p>

                <form
                    onSubmit={handleChangePassword}
                    style={{
                        display: "flex",
                        flexDirection: "column",
                        gap: "12px",
                        maxWidth: "460px",
                        marginTop: "20px",
                    }}
                >
                    <input
                        className="app-input"
                        type="password"
                        placeholder="Nuvarande lösenord"
                        value={currentPassword}
                        onChange={(e) => setCurrentPassword(e.target.value)}
                        required
                    />

                    <input
                        className="app-input"
                        type="password"
                        placeholder="Nytt lösenord"
                        value={newPassword}
                        onChange={(e) => setNewPassword(e.target.value)}
                        required
                    />

                    <input
                        className="app-input"
                        type="password"
                        placeholder="Bekräfta nytt lösenord"
                        value={confirmPassword}
                        onChange={(e) => setConfirmPassword(e.target.value)}
                        required
                    />

                    <button className="app-button" type="submit" disabled={loading}>
                        {loading ? "Sparar..." : "Byt lösenord"}
                    </button>
                </form>

                {error && <p className="app-message-error">{error}</p>}
                {successMessage && <p className="app-message-success">{successMessage}</p>}
            </section>
        </div>
    );
}

export default ProfilePage;