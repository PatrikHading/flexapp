function DashboardPage({ user }) {
    return (
        <div style={styles.wrapper}>
            <div style={styles.heroCard}>
                <h1>Dashboard</h1>
                <p style={styles.subtitle}>
                    Välkommen tillbaka, {user?.firstName}.
                </p>
            </div>

            <div style={styles.grid}>
                <div style={styles.card}>
                    <h2>Min profil</h2>
                    <p>
                        Namn: {user?.firstName} {user?.lastName}
                    </p>
                    <p>E-post: {user?.email}</p>
                    <p>Roll: {user?.role}</p>
                </div>

                <div style={styles.card}>
                    <h2>Status</h2>
                    <p>Konto: {user?.active ? "Aktivt" : "Inaktivt"}</p>
                    <p>ID: {user?.id}</p>
                </div>
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
    heroCard: {
        background: "#fff",
        padding: "24px",
        borderRadius: "12px",
        boxShadow: "0 8px 24px rgba(0,0,0,0.06)",
    },
    subtitle: {
        marginTop: "8px",
        color: "#555",
    },
    grid: {
        display: "grid",
        gridTemplateColumns: "repeat(auto-fit, minmax(260px, 1fr))",
        gap: "20px",
    },
    card: {
        background: "#fff",
        padding: "24px",
        borderRadius: "12px",
        boxShadow: "0 8px 24px rgba(0,0,0,0.06)",
    },
};

export default DashboardPage;