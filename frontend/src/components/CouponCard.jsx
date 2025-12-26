import React from 'react';
import { Copy, Calendar, MoreVertical, Trash2, Edit2 } from 'lucide-react';

const CouponCard = ({ coupon, onDelete, onEdit }) => {
    const isExpired = () => {
        if (!coupon.expiryDate) return false;
        const todayStr = new Date().toLocaleDateString('en-CA'); // YYYY-MM-DD
        const expiryStr = coupon.expiryDate.split('T')[0];
        return expiryStr < todayStr;
    };

    const isExpiringSoon = () => {
        if (!coupon.expiryDate || isExpired()) return false;
        const expiry = new Date(coupon.expiryDate);
        const now = new Date();
        const diffMs = expiry - now;
        const diffDays = Math.ceil(diffMs / (1000 * 60 * 60 * 24));
        return diffDays >= 0 && diffDays <= 7;
    };

    const getStatusColor = () => {
        if (isExpired()) return 'border-red-500/50 bg-red-900/10';
        if (isExpiringSoon()) return 'border-yellow-500/50 bg-yellow-900/10';
        return 'border-gray-800 bg-gray-900';
    };

    const copyCode = () => {
        navigator.clipboard.writeText(coupon.code);
        // Could add toast here
    };

    return (
        <div className={`relative rounded-xl border p-5 transition-all hover:shadow-lg ${getStatusColor()}`}>
            <div className="flex justify-between items-start mb-4">
                <div>
                    <h3 className="text-base md:text-lg font-bold text-gray-100">{coupon.merchant}</h3>
                    <p className="text-sm text-gray-400">{coupon.description}</p>
                </div>
                <div className="flex gap-2">
                    <button onClick={() => onEdit(coupon)} className="p-1.5 text-gray-400 hover:text-indigo-400 transition-colors">
                        <Edit2 className="w-4 h-4" />
                    </button>
                    <button onClick={() => onDelete(coupon.id)} className="p-1.5 text-gray-400 hover:text-red-400 transition-colors">
                        <Trash2 className="w-4 h-4" />
                    </button>
                </div>
            </div>

            <div className="bg-gray-950 rounded-lg p-3 flex justify-between items-center mb-4 group cursor-pointer border border-gray-800" onClick={copyCode}>
                <code className="text-indigo-400 font-mono text-base md:text-lg tracking-wider font-semibold">
                    {coupon.code}
                </code>
                <Copy className="w-4 h-4 text-gray-500 group-hover:text-white transition-colors" />
            </div>

            <div className="flex items-center gap-2 text-sm text-gray-500">
                <Calendar className="w-4 h-4" />
                {coupon.expiryDate ? new Date(coupon.expiryDate).toLocaleDateString() : 'No Expiry'}
                {isExpired() && <span className="text-red-500 font-medium ml-auto">Expired</span>}
                {isExpiringSoon() && <span className="text-yellow-500 font-medium ml-auto">Expiring Soon</span>}
            </div>

            {(coupon.discountDetails || coupon.minAmount) && (
                <div className="mt-3 text-sm text-gray-400 border-t border-gray-800 pt-3 flex flex-wrap gap-4">
                    {coupon.discountDetails && (
                        <span>{coupon.discountDetails}</span>
                    )}
                    {coupon.minAmount && (
                        <span className="text-indigo-400 font-medium">
                            Min Order: ₹{coupon.minAmount}
                        </span>
                    )}
                </div>
            )}
        </div>
    );
};

export default CouponCard;
