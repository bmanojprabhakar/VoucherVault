import React, { useState, useEffect } from 'react';
import { X } from 'lucide-react';

const CouponModal = ({ isOpen, onClose, onSubmit, initialData }) => {
    const [formData, setFormData] = useState({
        merchant: '',
        code: '',
        description: '',
        discountDetails: '',
        expiryDate: '',
        minAmount: ''
    });

    const [error, setError] = useState('');
    const [autoUppercase, setAutoUppercase] = useState(true);

    useEffect(() => {
        setError(''); // Clear error on open/initialData change
        if (initialData) {
            setFormData({
                merchant: initialData.merchant || '',
                code: initialData.code || '',
                description: initialData.description || '',
                discountDetails: initialData.discountDetails || '',
                expiryDate: initialData.expiryDate ? initialData.expiryDate.split('T')[0] : '',
                minAmount: initialData.minAmount || ''
            });
        } else {
            setFormData({
                merchant: '',
                code: '',
                description: '',
                discountDetails: '',
                expiryDate: '',
                minAmount: ''
            });
        }
    }, [initialData, isOpen]);

    if (!isOpen) return null;

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        // Frontend Validation
        if (formData.expiryDate) {
            const selectedDate = new Date(formData.expiryDate);
            const today = new Date();
            today.setHours(0, 0, 0, 0);

            // Allow today, reject only strictly past days
            // Note: selectedDate is in UTC from input date string usually, but 'YYYY-MM-DD' parses to UTC midnight.
            // new Date('2023-12-25') -> UTC midnight.
            // Today local might be different. 
            // Better to compare strings to avoid timezone mess for "dates".
            const todayStr = new Date().toLocaleDateString('en-CA'); // YYYY-MM-DD local
            if (formData.expiryDate < todayStr) {
                setError('Expiry date cannot be in the past.');
                return;
            }
        }

        try {
            await onSubmit({
                ...formData,
                expiryDate: formData.expiryDate ? `${formData.expiryDate}T00:00:00` : null
            });
        } catch (err) {
            console.error(err);
            setError(err.response?.data?.error || 'Failed to save coupon. Please try again.');
        }
    };

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm">
            <div className="bg-gray-900 rounded-2xl w-full max-w-lg border border-gray-800 shadow-2xl max-h-[90vh] overflow-y-auto">
                <div className="flex justify-between items-center p-6 border-b border-gray-800 sticky top-0 bg-gray-900 z-10">
                    <h2 className="text-xl font-bold text-white">{initialData ? 'Edit Coupon' : 'Add New Coupon'}</h2>
                    <button onClick={onClose} className="text-gray-400 hover:text-white">
                        <X className="w-6 h-6" />
                    </button>
                </div>

                <form onSubmit={handleSubmit} className="p-6 space-y-4">
                    {error && (
                        <div className="p-3 text-sm text-red-500 bg-red-500/10 border border-red-500/20 rounded-lg">
                            {error}
                        </div>
                    )}
                    <div>
                        <label className="block text-sm font-medium text-gray-400 mb-1">Merchant <span className="text-red-500">*</span></label>
                        <input
                            required
                            type="text"
                            value={formData.merchant}
                            onChange={e => setFormData({ ...formData, merchant: e.target.value })}
                            className="w-full bg-gray-950 border border-gray-800 rounded-lg p-2.5 text-white focus:ring-2 focus:ring-indigo-600 focus:border-transparent outline-none"
                            placeholder="e.g. Amazon"
                        />
                    </div>

                    <div>
                        <div className="flex justify-between items-center mb-1">
                            <label className="text-sm font-medium text-gray-400">Coupon Code <span className="text-red-500">*</span></label>
                            <label className="flex items-center gap-2 cursor-pointer group">
                                <input
                                    type="checkbox"
                                    checked={autoUppercase}
                                    onChange={e => setAutoUppercase(e.target.checked)}
                                    className="w-4 h-4 rounded border-gray-700 bg-gray-900 text-indigo-600 focus:ring-indigo-600 focus:ring-offset-gray-900 transition-colors"
                                />
                                <span className="text-xs text-gray-500 group-hover:text-gray-300 transition-colors">Auto Caps</span>
                            </label>
                        </div>
                        <input
                            required
                            type="text"
                            value={formData.code}
                            onChange={e => {
                                const val = e.target.value;
                                setFormData({
                                    ...formData,
                                    code: autoUppercase ? val.toUpperCase() : val
                                });
                            }}
                            className="w-full bg-gray-950 border border-gray-800 rounded-lg p-2.5 text-white focus:ring-2 focus:ring-indigo-600 focus:border-transparent outline-none font-mono"
                            placeholder="SAVE20"
                        />
                    </div>

                    <div className="grid grid-cols-2 gap-4">
                        <div>
                            <label className="block text-sm font-medium text-gray-400 mb-1">Expiry Date <span className="text-red-500">*</span></label>
                            <input
                                required
                                type="date"
                                min={new Date().toISOString().split('T')[0]}
                                value={formData.expiryDate}
                                onChange={e => setFormData({ ...formData, expiryDate: e.target.value })}
                                className="w-full bg-gray-950 border border-gray-800 rounded-lg p-2.5 text-white focus:ring-2 focus:ring-indigo-600 focus:border-transparent outline-none"
                            />
                        </div>
                        <div>
                            <label className="block text-sm font-medium text-gray-400 mb-1">Min Order Value</label>
                            <input
                                type="number"
                                step="0.01"
                                value={formData.minAmount}
                                onChange={e => setFormData({ ...formData, minAmount: e.target.value })}
                                className="w-full bg-gray-950 border border-gray-800 rounded-lg p-2.5 text-white focus:ring-2 focus:ring-indigo-600 focus:border-transparent outline-none"
                                placeholder="0.00"
                            />
                        </div>
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-400 mb-1">Discount Details</label>
                        <input
                            type="text"
                            value={formData.discountDetails}
                            onChange={e => setFormData({ ...formData, discountDetails: e.target.value })}
                            className="w-full bg-gray-950 border border-gray-800 rounded-lg p-2.5 text-white focus:ring-2 focus:ring-indigo-600 focus:border-transparent outline-none"
                            placeholder="e.g. 20% Off or $10 Cashback"
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-400 mb-1">Description / Notes</label>
                        <textarea
                            value={formData.description}
                            onChange={e => setFormData({ ...formData, description: e.target.value })}
                            className="w-full bg-gray-950 border border-gray-800 rounded-lg p-2.5 text-white focus:ring-2 focus:ring-indigo-600 focus:border-transparent outline-none h-24 resize-none"
                            placeholder="Received via email..."
                        />
                    </div>

                    <button
                        type="submit"
                        className="w-full bg-indigo-600 hover:bg-indigo-700 text-white font-semibold py-3 rounded-lg transition-colors mt-4"
                    >
                        {initialData ? 'Update Coupon' : 'Save Coupon'}
                    </button>
                </form>
            </div>
        </div>
    );
};

export default CouponModal;
