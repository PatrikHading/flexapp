import { useEffect, useState } from "react";
import { fetchTodayTimeEntry, postTimeAction } from "../services/auth";

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

function TimePage({ user }) {
    const [timeEntry, setTimeEntry] = useState(null);
    const [loading, setLoading] = useState(true);
    const [actionLoading, setActionLoading] = useState("");
    const [error, setError] = useState("");

    const loadTodayEntry = async () => {
        try {
            setLoading(true);
            setError("");

            const data = await fetchTodayTimeEntry(user.id);
            setTimeEntry(data);
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (user?.id) {
            loadTodayEntry();
        }
    }, [user]);

    const handleAction = async (action) => {
        try {
            setError("");
            setActionLoading(action);

            const updatedEntry = await postTimeAction(user.id, action);
            setTimeEntry(updatedEntry);
        } catch (err) {
            setError(err.message);
        } finally {
            setActionLoading("");
        }
    };

    if (loading) {
        return (
            <div style={styles.card}>
                <h1>Tidrapportering</h1>
                <p>Laddar dagens tidrapport...</p>
            </div>
        );
    }

    return (
        <div style={styles.wrapper}>
            <div style={styles.card}>
                <h1>Tidrapportering</h1>
                <p style={styles.subtitle}>
                    Här kan du registrera dagens arbetstid.
                </p>

                {error && <p style={styles.error}>{error}</p>}

                <div style={styles.buttonRow}>
                    <button
                        style={styles.button}
                        onClick={() => handleAction("check-in")}
                        disabled={actionLoading !== ""}
                    >
                        {actionLoading === "check-in" ? "Sparar..." : "Checka in"}
                    </button>

                    <button
                        style={styles.button}
                        onClick={() => handleAction("lunch-out")}
                        disabled={actionLoading !== ""}
                    >
                        {actionLoading === "lunch-out" ? "Sparar..." : "Lunch ut"}
                    </button>

                    <button
                        style={styles.button}
                        onClick={() => handleAction("lunch-in")}
                        disabled={actionLoading !== ""}
                    >
                        {actionLoading === "lunch-in" ? "Sparar..." : "Lunch in"}
                    </button>

                    <button
                        style={styles.button}
                        onClick={() => handleAction("check-out")}
                        disabled={actionLoading !== ""}
                    >
                        {actionLoading === "check-out" ? "Sparar..." : "Checka ut"}
                    </button>
                </div>
            </div>

            <div style={styles.card}>
                <h2>Dagens status</h2>

                {!timeEntry ? (
                    <p>Ingen tidrapport finns registrerad för idag ännu.</p>
                ) : (
                    <div style={styles.grid}>
                        <div style={styles.infoItem}>
                            <span style={styles.label}>Datum</span>
                            <span style={styles.value}>{timeEntry.workDate || "-"}</span>
                        </div>

                        <div style={styles.infoItem}>
                            <span style={styles.label}>Check-in</span>
                            <span style={styles.value}>
                {formatDateTime(timeEntry.checkInTime)}
              </span>
                        </div>

                        <div style={styles.infoItem}>
                            <span style={styles.label}>Lunch ut</span>
                            <span style={styles.value}>
                {formatDateTime(timeEntry.lunchOutTime)}
              </span>
                        </div>

                        <div style={styles.infoItem}>
                            <span style={styles.label}>Lunch in</span>
                            <span style={styles.value}>
                {formatDateTime(timeEntry.lunchInTime)}
              </span>
                        </div>

                        <div style={styles.infoItem}>
                            <span style={styles.label}>Check-out</span>
                            <span style={styles.value}>
                {formatDateTime(timeEntry.checkOutTime)}
              </span>
                        </div>

                        <div style={styles.infoItem}>
                            <span style={styles.label}>Arbetade minuter</span>
                            <span style={styles.value}>
                {timeEntry.workedMinutes ?? "-"}
              </span>
                        </div>

                        <div style={styles.infoItem}>
                            <span style={styles.label}>Lunchminuter</span>
                            <span style={styles.value}>
                {timeEntry.lunchMinutes ?? "-"}
              </span>
                        </div>

                        <div style={styles.infoItem}>
                            <span style={styles.label}>Flex minuter</span>
                            <span style={styles.value}>
                {timeEntry.flexMinutes ?? "-"}
              </span>
                        </div>
                    </div>
                )}
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
    buttonRow: {
        display: "flex",
        flexWrap: "wrap",
        gap: "12px",
        marginTop: "20px",
    },
    button: {
        padding: "12px 16px",
        fontSize: "15px",
        borderRadius: "8px",
        border: "none",
        cursor: "pointer",
        background: "#222",
        color: "#fff",
    },
    grid: {
        display: "grid",
        gridTemplateColumns: "repeat(auto-fit, minmax(220px, 1fr))",
        gap: "16px",
        marginTop: "20px",
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
        marginTop: "16px",
    },
};

export default TimePage;