function HistoryPage() {
    return (
        <div style={styles.card}>
            <h1>Historik</h1>
            <p>Här kommer tidigare arbetspass och flexhistorik att visas senare.</p>
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
};

export default HistoryPage;