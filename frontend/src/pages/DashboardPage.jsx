function DashboardPage({ user }) {
    return (
        <div style={styles.wrapper}>
            <h1>Dashboard</h1>
            <p>Välkommen tillbaka, {user?.firstName}.</p>

            <div style={styles.card}>
                <h2>Inloggad användare</h2>
                <pre style={styles.pre}>{JSON.stringify(user, null, 2)}</pre>
            </div>
        </div>
    );
}

const styles = {
    wrapper: {
        display: "flex",
        flexDirection: "column",
        gap: "20px",
    },
    card: {
        background: "#fff",
        padding: "24px",
        borderRadius: "12px",
        boxShadow: "0 8px 24px rgba(0,0,0,0.06)",
    },
    pre: {
        background: "#f7f7f7",
        padding: "12px",
        borderRadius: "8px",
        overflowX: "auto",
        fontSize: "14px",
    },
};

export default DashboardPage;