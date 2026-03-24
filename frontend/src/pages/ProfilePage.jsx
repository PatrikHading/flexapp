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
        <div style={styles.wrapper}>
            <div style={styles.card}>
                <h1>Min profil</h1>
                <p style={styles.subtitle}>
                    Här kan du se dina användaruppgifter.
                </p>

                <div style={styles.infoGrid}>
                    <div style={styles.infoItem}>
                        <span style={styles.label}>Förnamn</span>
                        <span style={styles.value}>{user?.firstName || "-"}</span>
                    </div>

                    <div style={styles.infoItem}>
                        <span style={styles.label}>Efternamn</span>
                        <span style={styles.value}>{user?.lastName || "-"}</span>
                    </div>

                    <div style={styles.infoItem}>
                        <span style={styles.label}>E-post</span>
                        <span style={styles.value}>{user?.email || "-"}</span>
                    </div>

                    <div style={styles.infoItem}>
                        <span style={styles.label}>Roll</span>
                        <span style={styles.value}>{user?.role || "-"}</span>
                    </div>

                    <div style={styles.infoItem}>
                        <span style={styles.label}>Status</span>
                        <span style={styles.value}>
              {user?.active ? "Aktiv" : "Inaktiv"}
            </span>
                    </div>

                    <div style={styles.infoItem}>
                        <span style={styles.label}>Användar-ID</span>
                        <span style={styles.value}>{user?.id || "-"}</span>
                    </div>
                </div>
            </div>

            <div style={styles.card}>
                <h2>Byt lösenord</h2>
                <p style={styles.subtitle}>
                    Ange ditt nuvarande lösenord och välj ett nytt.
                </p>

                <form onSubmit={handleChangePassword} style={styles.form}>
                    <input
                        type="password"
                        placeholder="Nuvarande lösenord"
                        value={currentPassword}
                        onChange={(e) => setCurrentPassword(e.target.value)}
                        style={styles.input}
                        required
                    />

                    <input
                        type="password"
                        placeholder="Nytt lösenord"
                        value={newPassword}
                        onChange={(e) => setNewPassword(e.target.value)}
                        style={styles.input}
                        required
                    />

                    <input
                        type="password"
                        placeholder="Bekräfta nytt lösenord"
                        value={confirmPassword}
                        onChange={(e) => setConfirmPassword(e.target.value)}
                        style={styles.input}
                        required
                    />

                    <button type="submit" style={styles.button} disabled={loading}>
                        {loading ? "Sparar..." : "Byt lösenord"}
                    </button>
                </form>

                {error && <p style={styles.error}>{error}</p>}
                {successMessage && <p style={styles.success}>{successMessage}</p>}
            </div>
        </div>
    );
}

const styles = {
    wrapper: {
        display: "flex",
        flexDirection: "column",
        gap: "24px",
    },
    card: {
        background: "#fff",
        padding: "24px",
        borderRadius: "12px",
        boxShadow: "0 8px 24px rgba(0,0,0,0.06)",
    },
    subtitle: {
        marginTop: "8px",
        color: "#555",
    },
    infoGrid: {
        display: "grid",
        gridTemplateColumns: "repeat(auto-fit, minmax(220px, 1fr))",
        gap: "16px",
        marginTop: "24px",
    },
    infoItem: {
        display: "flex",
        flexDirection: "column",
        gap: "6px",
        padding: "14px",
        borderRadius: "10px",
        background: "#f7f7f7",
    },
    label: {
        fontSize: "13px",
        color: "#666",
        fontWeight: "600",
    },
    value: {
        fontSize: "16px",
        color: "#111",
    },
    form: {
        display: "flex",
        flexDirection: "column",
        gap: "12px",
        marginTop: "20px",
        maxWidth: "420px",
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
    success: {
        color: "green",
        marginTop: "12px",
    },
};

export default ProfilePage;