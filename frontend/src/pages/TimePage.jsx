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
            <div className="app-card">
                <h1>Tidrapportering</h1>
                <p className="app-card-subtitle">Laddar dagens tidrapport...</p>
            </div>
        );
    }

    return (
        <div className="page-section">
            <section className="app-card-hero">
                <h1 className="app-card-title">Tidrapportering</h1>
                <p className="app-card-subtitle">
                    Registrera dagens arbetstid och följ status i realtid.
                </p>
            </section>

            <section className="app-card">
                <h2>Åtgärder</h2>

                {error && <p className="app-message-error">{error}</p>}

                <div className="app-grid dashboard-action-grid">
                    <button
                        className="app-button"
                        onClick={() => handleAction("check-in")}
                        disabled={actionLoading !== ""}
                    >
                        {actionLoading === "check-in" ? "Sparar..." : "Checka in"}
                    </button>

                    <button
                        className="app-button"
                        onClick={() => handleAction("lunch-out")}
                        disabled={actionLoading !== ""}
                    >
                        {actionLoading === "lunch-out" ? "Sparar..." : "Lunch ut"}
                    </button>

                    <button
                        className="app-button"
                        onClick={() => handleAction("lunch-in")}
                        disabled={actionLoading !== ""}
                    >
                        {actionLoading === "lunch-in" ? "Sparar..." : "Lunch in"}
                    </button>

                    <button
                        className="app-button"
                        onClick={() => handleAction("check-out")}
                        disabled={actionLoading !== ""}
                    >
                        {actionLoading === "check-out" ? "Sparar..." : "Checka ut"}
                    </button>
                </div>
            </section>

            <section className="app-card">
                <h2>Dagens status</h2>

                {!timeEntry ? (
                    <p className="app-card-subtitle">
                        Ingen tidrapport finns registrerad för idag ännu.
                    </p>
                ) : (
                    <div className="app-grid">
                        <div className="app-info-box">
                            <span className="app-label">Datum</span>
                            <span className="app-value">{timeEntry.workDate || "-"}</span>
                        </div>

                        <div className="app-info-box">
                            <span className="app-label">Check-in</span>
                            <span className="app-value">
                {formatDateTime(timeEntry.checkInTime)}
              </span>
                        </div>

                        <div className="app-info-box">
                            <span className="app-label">Lunch ut</span>
                            <span className="app-value">
                {formatDateTime(timeEntry.lunchOutTime)}
              </span>
                        </div>

                        <div className="app-info-box">
                            <span className="app-label">Lunch in</span>
                            <span className="app-value">
                {formatDateTime(timeEntry.lunchInTime)}
              </span>
                        </div>

                        <div className="app-info-box">
                            <span className="app-label">Check-out</span>
                            <span className="app-value">
                {formatDateTime(timeEntry.checkOutTime)}
              </span>
                        </div>

                        <div className="app-info-box">
                            <span className="app-label">Arbetade minuter</span>
                            <span className="app-value">
                {timeEntry.workedMinutes ?? "-"}
              </span>
                        </div>

                        <div className="app-info-box">
                            <span className="app-label">Lunchminuter</span>
                            <span className="app-value">
                {timeEntry.lunchMinutes ?? "-"}
              </span>
                        </div>

                        <div className="app-info-box">
                            <span className="app-label">Flex minuter</span>
                            <span className="app-value">
                {timeEntry.flexMinutes ?? "-"}
              </span>
                        </div>
                    </div>
                )}
            </section>
        </div>
    );
}

export default TimePage;