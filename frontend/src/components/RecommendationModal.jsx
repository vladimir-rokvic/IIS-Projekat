import { useState } from "react";
import "../pages/CampaignCoordinatorDashboard.css";

const formatDate = (dateValue) => {
    if (!dateValue) return "-";
    return new Date(dateValue).toLocaleDateString("en-GB");
};

const RecommendationsModal = ({ recommendations, onClose, onClick }) => {
    const [hoveredIndex, setHoveredIndex] = useState(null);
    const [mousePos, setMousePos] = useState({ x: 0, y: 0 });

    const handleMouseEnter = (e, index) => {
        setMousePos({ x: e.clientX, y: e.clientY });
        setHoveredIndex(index);
    };

    const handleMouseMove = (e) => {
        setMousePos({ x: e.clientX, y: e.clientY });
    };

    return (
        <div className="campaign-modal-overlay" onClick={onClose}>
            <div className="campaign-modal campaign-form-card" onClick={(e) => e.stopPropagation()}>
                <div className="campaign-modal-header">
                    <div className="campaign-modal-header-content">
                        <h2>Recommended Campaigns</h2>
                        <span className="campaign-modal-subtitle">
                            Hover over a recommendation to see similar past campaigns
                        </span>
                    </div>
                    <button className="campaign-modal-close" onClick={onClose}>✕</button>
                </div>

                <div className="campaign-card-grid">
                    {recommendations.map((rec, index) => (
                        <button
                            key={index}
                            className="campaign-card campaign-card-button"
                            onClick={() => onClick(rec)}
                            onMouseEnter={(e) => handleMouseEnter(e, index)}
                            onMouseMove={handleMouseMove}
                            onMouseLeave={() => setHoveredIndex(null)}
                        >
                            <div className="campaign-card-header">
                                <div className="campaign-card-title">{rec.recommendedCategory}</div>
                                <div className="campaign-status-pill">{rec.recommendedDurationDays} days</div>
                            </div>
                            <div className="campaign-card-text">
                                Goal: ${rec.recommendedGoal.toLocaleString()}
                            </div>
                            <div className="campaign-card-text">
                                Duration: {rec.recommendedDurationDays} days
                            </div>
                            <div className="campaign-card-footer">
                                <span>Based on {rec.referenceCampaigns.length} past campaigns</span>
                            </div>
                        </button>
                    ))}
                </div>
            </div>

            {/* Floating popup anchored to mouse position */}
            {hoveredIndex !== null && (
                <div
                    className="campaign-panel"
                    onClick={(e) => e.stopPropagation()}
                    style={{
                        position: "fixed",
                        top: mousePos.y + 16,
                        left: mousePos.x + 16,
                        width: 320,
                        zIndex: 200,
                        pointerEvents: "none",
                    }}
                >
                    <div className="campaign-card-title" style={{ marginBottom: 8 }}>
                        Reference Campaigns
                    </div>
                    {recommendations[hoveredIndex].referenceCampaigns.map((ref) => (
                        <div key={ref.id} style={{ borderTop: "1px solid #c7c1b8", paddingTop: 8, marginTop: 8 }}>
                            <div className="campaign-card-header">
                                <div className="campaign-card-title" style={{ fontSize: "0.9rem" }}>
                                    {ref.name}
                                </div>
                                <div className="campaign-status-pill">{ref.status}</div>
                            </div>
                            <div className="campaign-card-text">
                                {formatDate(ref.startDate)} - {formatDate(ref.endDate)}
                            </div>
                            <div className="campaign-card-text">
                                Raised: ${ref.raised.toLocaleString()} / ${ref.goal.toLocaleString()}
                            </div>
                            <div className="campaign-progress-bar" style={{ marginTop: 4 }}>
                                <div
                                    className="campaign-progress-fill"
                                    style={{ width: `${Math.min((ref.raised / ref.goal) * 100, 100)}%` }}
                                />
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default RecommendationsModal;