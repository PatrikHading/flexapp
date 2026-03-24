import { useEffect, useState } from "react";
import { fetchTimeHistory } from "../services/auth";

const formatDateTime = (value) => {
    if (!value) return "-";

    try {
        const date = new Date(value);

        if (Number.isNaN(date.getTime())) {
            return value;
        }

        return date.toLocaleString("sv-SE");
    } catch {
        return value;
    }
};

function HistoryPage({ user }) {
    const [historyEntries, setHistoryEntries] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        const loadHistory = async () => {
            try {
                setLoading(true);
                setError("");

                const data = await fetchTimeHistory(user.id);
                setHistoryEntries(Array.isArray(data) ? data : []);
            } catch (err) {
                setError(err.message);
            } finally {
                setLoading(false);
            }
        };

        if (user?.id) {
            loadHistory();
        }
    }, [user]);

    if (loading) {
        return (
            <div style={styles.card}>
                <h1>Historik</h1>
                <p>Laddar historik...</p>
            </div>
        );
    }

    if (error) {
        return (
            <div style={styles.card}>
                <h1>Historik</h1>
                <p style={styles.error}>{error}</p>
            </div>
        );
    }

    if (historyEntries.length === 0) {
        return (
            <div style={styles.card}>
                <h1>Historik</h1>
                <p>Det finns ingen historik att visa ännu.</p>
            </div>
        );
    }

    return (
        <div style={styles.wrapper}>
            <div style={styles.card}>
                <h1>Historik</h1>
                <p style={styles.subtitle}>
                    Här ser du dina tidigare tidrapporter.
                </p>
            </div>

            <div style={styles.list}>
                {historyEntries.map((entry) => (
                    <div key={entry.id} style={styles.entryCard}>
                        <div style={styles.entryHeader}>
                            <h2 style={styles.entryTitle}>{entry.workDate || "Okänt datum"}</h2>
                        </div>

                        <div style={styles.grid}>
                            <div style={styles.infoItem}>
                                <span style={styles.label}>Check-in</span>
                                <span style={styles.value}>
                  {formatDateTime(entry.checkInTime)}
                </span>
                            </div>

                            <div style={styles.infoItem}>
                                <span style={styles.label}>Lunch ut</span>
                                <span style={styles.value}>
                  {formatDateTime(entry.lunchOutTime)}
                </span>
                            </div>

                            <div style={styles.infoItem}>
                                <span style={styles.label}>Lunch in</span>
                                <span style={styles.value}>
                  {formatDateTime(entry.lunchInTime)}
                </span>
                            </div>

                            <div style={styles.infoItem}>
                                <span style={styles.label}>Check-out</span>
                                <span style={styles.value}>
                  {formatDateTime(entry.checkOutTime)}
                </span>
                            </div>

                            <div style={styles.infoItem}>
                                <span style={styles.label}>Arbetade minuter</span>
                                <span style={styles.value}>
                  {entry.workedMinutes ?? "-"}
                </span>
                            </div>

                            <div style={styles.infoItem}>
                                <span style={styles.label}>Lunchminuter</span>
                                <span style={styles.value}>
                  {entry.lunchMinutes ?? "-"}
                </span>
                            </div>

                            <div style={styles.infoItem}>
                                <span style={styles.label}>Flex minuter</span>
                                <span style={styles.value}>
                  {entry.flexMinutes ?? "-"}
                </span>
                            </div>
                        </div>
                    </div>
                ))}
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
    list: {
        display: "flex",
        flexDirection: "column",
        gap: "20px",
    },
    entryCard: {
        background: "#fff",
        padding: "24px",
        borderRadius: "12px",
        boxShadow: "0 8px 24px rgba(0,0,0,0.06)",
    },
    entryHeader: {
        marginBottom: "16px",
    },
    entryTitle: {
        margin: 0,
        fontSize: "20px",
    },
    grid: {
        display: "grid",
        gridTemplateColumns: "repeat(auto-fit, minmax(220px, 1fr))",
        gap: "16px",
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
    error: {
        color: "crimson",
    },
};

export default HistoryPage;