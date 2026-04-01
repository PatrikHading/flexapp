import { useEffect, useState } from "react";
import {
    fetchMySchedule,
    fetchTodayTimeEntry,
    fetchFlexBalance,
    postTimeAction,
} from "../services/auth";

const formatTime = (time) => {
    if (!time) return "-";

    if (typeof time === "string" && time.includes(":")) {
        return time.slice(0, 5);
    }

    return time;
};

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

function DashboardPage({ user }) {
    const [schedule, setSchedule] = useState(null);
    const [timeEntry, setTimeEntry] = useState(null);
    const [flexBalance, setFlexBalance] = useState(null);
    const [loading, setLoading] = useState(true);
    const [actionLoading, setActionLoading] = useState("");
    const [error, setError] = useState("");

    const loadDashboardData = async () => {
        try {
            setLoading(true);
            setError("");

            const [scheduleData, timeData, flexData] = await Promise.all([
                fetchMySchedule(),
                fetchTodayTimeEntry(),
                fetchFlexBalance(),
            ]);

            setSchedule(Array.isArray(scheduleData) ? scheduleData[0] : scheduleData);
            setTimeEntry(timeData);
            setFlexBalance(flexData);
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (user?.id) {
            loadDashboardData();
        }
    }, [user]);

    const handleAction = async (action) => {
        try {
            setError("");
            setActionLoading(action);

            await postTimeAction(action);
            await loadDashboardData();
        } catch (err) {
            setError(err.message);
        } finally {
            setActionLoading("");
        }
    };

    if (loading) {
        return (
            <div className="app-card">
                <h1>Dashboard</h1>
                <p className="app-card-subtitle">Laddar översikt...</p>
            </div>
        );
    }

    return (
        <div className="page-section">
            <section className="app-card-hero dashboard-hero">
                <div className="dashboard-hero-main">
                    <h1 className="app-card-title">Välkommen tillbaka, {user?.firstName}</h1>
                    <p className="app-card-subtitle">
                        Här ser du dagens status, schema och ditt aktuella flexsaldo.
                    </p>
                </div>

                <div className="dashboard-hero-actions">
                    <div className="dashboard-hero-actions-title">Snabbåtgärder</div>

                    <div className="dashboard-action-grid">
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
                </div>
            </section>

            {error && (
                <section className="app-card">
                    <p className="app-message-error">{error}</p>
                </section>
            )}

            <section className="app-grid">
                <div className="app-card">
                    <h2>Dagens schema</h2>

                    {!schedule ? (
                        <p className="app-card-subtitle">Inget schema registrerat för idag.</p>
                    ) : (
                        <div className="app-grid">
                            <div className="app-info-box">
                                <span className="app-label">Start</span>
                                <span className="app-value">
                  {formatTime(schedule.plannedStartTime)}
                </span>
                            </div>

                            <div className="app-info-box">
                                <span className="app-label">Slut</span>
                                <span className="app-value">
                  {formatTime(schedule.plannedEndTime)}
                </span>
                            </div>

                            <div className="app-info-box">
                                <span className="app-label">Arbetstid</span>
                                <span className="app-value">
                  {schedule.expectedWorkMinutes ?? "-"} min
                </span>
                            </div>

                            <div className="app-info-box">
                                <span className="app-label">Lunch</span>
                                <span className="app-value">
                  {schedule.paidLunchMinutes ?? "-"} min
                </span>
                            </div>
                        </div>
                    )}
                </div>

                <div className="app-card">
                    <h2>Flexsaldo</h2>

                    <div className="app-grid">
                        <div className="app-info-box">
                            <span className="app-label">Totalt flexsaldo</span>
                            <span className="app-value">
                {flexBalance?.totalFlexMinutes ?? "-"} min
              </span>
                        </div>

                        <div className="app-info-box">
                            <span className="app-label">Dagens flex</span>
                            <span className="app-value">
                {timeEntry?.flexMinutes ?? "-"} min
              </span>
                        </div>
                    </div>
                </div>
            </section>

            <section className="app-card">
                <h2>Dagens status</h2>

                {!timeEntry ? (
                    <p className="app-card-subtitle">
                        Ingen tidrapport registrerad för idag ännu.
                    </p>
                ) : (
                    <div className="app-grid">
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
                    </div>
                )}
            </section>
        </div>
    );
}

export default DashboardPage;