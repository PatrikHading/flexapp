import { useEffect, useState } from "react";
import { fetchUserSchedule } from "../services/auth";

const formatTime = (time) => {
    if (!time) return "-";

    // Om format "HH:mm:ss" → ta bara HH:mm
    if (typeof time === "string" && time.includes(":")) {
        return time.slice(0, 5);
    }

    return time;
};

function SchedulePage({ user }) {
    const [schedule, setSchedule] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        const loadSchedule = async () => {
            try {
                setLoading(true);
                setError("");

                const data = await fetchUserSchedule(user.id);
                console.log("Schedule response:", data);
                setSchedule(Array.isArray(data) ? data[0] : data);
            } catch (err) {
                setError(err.message);
            } finally {
                setLoading(false);
            }
        };

        if (user?.id) {
            loadSchedule();
        }
    }, [user]);

    if (loading) {
        return (
            <div style={styles.card}>
                <h1>Mitt schema</h1>
                <p>Laddar schema...</p>
            </div>
        );
    }

    if (error) {
        return (
            <div style={styles.card}>
                <h1>Mitt schema</h1>
                <p style={styles.error}>{error}</p>
            </div>
        );
    }

    if (!schedule) {
        return (
            <div style={styles.card}>
                <h1>Mitt schema</h1>
                <p>Det finns inget schema registrerat för dig ännu.</p>
            </div>
        );
    }

    return (
        <div style={styles.wrapper}>
            <div style={styles.card}>
                <h1>Mitt schema</h1>
                <p style={styles.subtitle}>
                    Här ser du ditt registrerade arbetsschema.
                </p>

                <div style={styles.grid}>
                    <div style={styles.infoItem}>
                        <span style={styles.label}>Starttid</span>
                        <span style={styles.value}>{formatTime(schedule.plannedStartTime)}</span>
                    </div>

                    <div style={styles.infoItem}>
                        <span style={styles.label}>Sluttid</span>
                        <span style={styles.value}>{formatTime(schedule.plannedEndTime)}</span>
                    </div>

                    <div style={styles.infoItem}>
                        <span style={styles.label}>Arbetstid (minuter)</span>
                        <span style={styles.value}>
              {schedule.expectedWorkMinutes ?? "-"}
            </span>
                    </div>

                    <div style={styles.infoItem}>
                        <span style={styles.label}>Betald lunch (minuter)</span>
                        <span style={styles.value}>
              {schedule.paidLunchMinutes ?? "-"}
            </span>
                    </div>

                    <div style={styles.infoItem}>
                        <span style={styles.label}>Datum</span>
                        <span style={styles.value}>{schedule.workDate || "-"}</span>
                    </div>

                    <div style={styles.infoItem}>
                        <span style={styles.label}>Schema-ID</span>
                        <span style={styles.value}>{schedule.id || "-"}</span>
                    </div>
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
    grid: {
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
    error: {
        color: "crimson",
    },
};

export default SchedulePage;