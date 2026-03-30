import { useEffect, useState } from "react";
import {
    fetchTodayTimeEntry,
    postTimeAction,
    createManualTimeEntry,
} from "../services/auth";

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

const combineDateAndTime = (date, time) => {
    if (!date || !time) return null;
    return `${date}T${time}:00`;
};

function TimePage({ user }) {
    const [timeEntry, setTimeEntry] = useState(null);
    const [loading, setLoading] = useState(true);
    const [actionLoading, setActionLoading] = useState("");
    const [error, setError] = useState("");

    const [manualWorkDate, setManualWorkDate] = useState("");
    const [manualCheckIn, setManualCheckIn] = useState("");
    const [manualLunchOut, setManualLunchOut] = useState("");
    const [manualLunchIn, setManualLunchIn] = useState("");
    const [manualCheckOut, setManualCheckOut] = useState("");
    const [manualComment, setManualComment] = useState("");

    const [manualLoading, setManualLoading] = useState(false);
    const [manualError, setManualError] = useState("");
    const [manualSuccess, setManualSuccess] = useState("");

    const loadTodayEntry = async () => {
        try {
            setLoading(true);
            setError("");

            const data = await fetchTodayTimeEntry();
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

            const updatedEntry = await postTimeAction(action);
            setTimeEntry(updatedEntry);
        } catch (err) {
            setError(err.message);
        } finally {
            setActionLoading("");
        }
    };

    const handleManualSubmit = async (e) => {
        e.preventDefault();
        setManualError("");
        setManualSuccess("");

        if (!manualWorkDate || !manualCheckIn || !manualCheckOut) {
            setManualError("Datum, check-in och check-out måste fyllas i.");
            return;
        }

        try {
            setManualLoading(true);

            await createManualTimeEntry({
                workDate: manualWorkDate,
                checkInTime: combineDateAndTime(manualWorkDate, manualCheckIn),
                lunchOutTime: combineDateAndTime(manualWorkDate, manualLunchOut),
                lunchInTime: combineDateAndTime(manualWorkDate, manualLunchIn),
                checkOutTime: combineDateAndTime(manualWorkDate, manualCheckOut),
                comment: manualComment,
            });

            setManualWorkDate("");
            setManualCheckIn("");
            setManualLunchOut("");
            setManualLunchIn("");
            setManualCheckOut("");
            setManualComment("");

            setManualSuccess("Manuell tidrapport har sparats.");
            await loadTodayEntry();
        } catch (err) {
            setManualError(err.message);
        } finally {
            setManualLoading(false);
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
                    Registrera dagens arbetstid eller lägg in ett pass i efterhand.
                </p>
            </section>

            <section className="app-card">
                <h2>Snabbregistrering idag</h2>

                {error && <p className="app-message-error">{error}</p>}

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
            </section>

            <section className="app-card">
                <h2>Manuell registrering i efterhand</h2>
                <p className="app-card-subtitle">
                    Använd detta om du glömt att registrera arbetstid en tidigare dag.
                </p>

                <form className="profile-form" onSubmit={handleManualSubmit}>
                    <div className="profile-form-grid">
                        <div>
                            <label className="app-label">Datum</label>
                            <input
                                className="app-input"
                                type="date"
                                value={manualWorkDate}
                                onChange={(e) => setManualWorkDate(e.target.value)}
                                required
                            />
                        </div>

                        <div>
                            <label className="app-label">Check-in</label>
                            <input
                                className="app-input"
                                type="time"
                                value={manualCheckIn}
                                onChange={(e) => setManualCheckIn(e.target.value)}
                                required
                            />
                        </div>

                        <div>
                            <label className="app-label">Lunch ut</label>
                            <input
                                className="app-input"
                                type="time"
                                value={manualLunchOut}
                                onChange={(e) => setManualLunchOut(e.target.value)}
                            />
                        </div>

                        <div>
                            <label className="app-label">Lunch in</label>
                            <input
                                className="app-input"
                                type="time"
                                value={manualLunchIn}
                                onChange={(e) => setManualLunchIn(e.target.value)}
                            />
                        </div>

                        <div>
                            <label className="app-label">Check-out</label>
                            <input
                                className="app-input"
                                type="time"
                                value={manualCheckOut}
                                onChange={(e) => setManualCheckOut(e.target.value)}
                                required
                            />
                        </div>

                        <div className="profile-form-full">
                            <label className="app-label">Kommentar</label>
                            <textarea
                                className="app-input"
                                rows="4"
                                value={manualComment}
                                onChange={(e) => setManualComment(e.target.value)}
                                placeholder="Exempel: Glömde registrera arbetspasset igår."
                            />
                        </div>
                    </div>

                    <div className="profile-form-actions">
                        <button className="app-button" type="submit" disabled={manualLoading}>
                            {manualLoading ? "Sparar..." : "Spara manuell registrering"}
                        </button>
                    </div>

                    {manualError && <p className="app-message-error">{manualError}</p>}
                    {manualSuccess && <p className="app-message-success">{manualSuccess}</p>}
                </form>
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
                            <span className="app-value">{formatDateTime(timeEntry.checkInTime)}</span>
                        </div>

                        <div className="app-info-box">
                            <span className="app-label">Lunch ut</span>
                            <span className="app-value">{formatDateTime(timeEntry.lunchOutTime)}</span>
                        </div>

                        <div className="app-info-box">
                            <span className="app-label">Lunch in</span>
                            <span className="app-value">{formatDateTime(timeEntry.lunchInTime)}</span>
                        </div>

                        <div className="app-info-box">
                            <span className="app-label">Check-out</span>
                            <span className="app-value">{formatDateTime(timeEntry.checkOutTime)}</span>
                        </div>

                        <div className="app-info-box">
                            <span className="app-label">Arbetade minuter</span>
                            <span className="app-value">{timeEntry.workedMinutes ?? "-"}</span>
                        </div>

                        <div className="app-info-box">
                            <span className="app-label">Lunchminuter</span>
                            <span className="app-value">{timeEntry.lunchMinutes ?? "-"}</span>
                        </div>

                        <div className="app-info-box">
                            <span className="app-label">Flex minuter</span>
                            <span className="app-value">{timeEntry.flexMinutes ?? "-"}</span>
                        </div>
                    </div>
                )}
            </section>
        </div>
    );
}

export default TimePage;