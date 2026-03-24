function ProfilePage({ user }) {
    return (
        <div style={styles.card}>
            <h1>Profil</h1>
            <p>Här kommer profilinställningar att visas senare.</p>

            <div style={styles.infoBox}>
                <p><strong>Namn:</strong> {user?.firstName} {user?.lastName}</p>
                <p><strong>E-post:</strong> {user?.email}</p>
                <p><strong>Roll:</strong> {user?.role}</p>
                <p><strong>Aktiv:</strong> {user?.active ? "Ja" : "Nej"}</p>
            </div>
        </div>
    );
}

const styles = {
    card: {
        background: "#fff",
        padding: "24px",
        borderRadius: "12px",
        boxShadow: "0 8px 24px rgba(0,0,0,0.06)",
    },
    infoBox: {
        marginTop: "20px",
        padding: "16px",
        borderRadius: "8px",
        background: "#f7f7f7",
    },
};

export default ProfilePage;