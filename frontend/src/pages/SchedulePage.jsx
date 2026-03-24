function SchedulePage() {
    return (
        <div style={styles.card}>
            <h1>Mitt schema</h1>
            <p>Här kommer användarens schema att visas senare.</p>
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

export default SchedulePage;