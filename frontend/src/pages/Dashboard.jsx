import React, { useEffect, useState } from 'react';
import { Plus, Search } from 'lucide-react';
import api from '../api';
import CouponCard from '../components/CouponCard';
import CouponModal from '../components/CouponModal';
import ConfirmationModal from '../components/ConfirmationModal';
import { useAuth } from '../context/AuthContext';

const Dashboard = () => {
    const { user } = useAuth();
    const [coupons, setCoupons] = useState([]);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [editingCoupon, setEditingCoupon] = useState(null);
    const [search, setSearch] = useState('');
    const [filter, setFilter] = useState('ALL');
    const [loading, setLoading] = useState(true);

    // Delete Confirmation State
    const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
    const [couponToDelete, setCouponToDelete] = useState(null);

    const fetchCoupons = async () => {
        setLoading(true);
        try {
            const { data } = await api.get('/coupons?size=100');
            setCoupons(data.content);
        } catch (error) {
            console.error('Failed to fetch coupons', error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchCoupons();
    }, []);

    const handleDeleteClick = (id) => {
        setCouponToDelete(id);
        setIsDeleteModalOpen(true);
    };

    const confirmDelete = async () => {
        if (!couponToDelete) return;
        try {
            await api.delete(`/coupons/${couponToDelete}`);
            fetchCoupons();
            setIsDeleteModalOpen(false);
            setCouponToDelete(null);
        } catch (error) {
            console.error(error);
        }
    };

    const handleEdit = (coupon) => {
        setEditingCoupon(coupon);
        setIsModalOpen(true);
    };

    const handleCreateOrUpdate = async (data) => {
        try {
            if (editingCoupon) {
                await api.put(`/coupons/${editingCoupon.id}`, data);
            } else {
                await api.post('/coupons', data);
            }
            setIsModalOpen(false);
            setEditingCoupon(null);
            fetchCoupons();
        } catch (error) {
            console.error('Failed to save coupon:', error);
            throw error; // Propagate to Modal for display
        }
    };

    const getStatus = (coupon) => {
        if (!coupon.expiryDate) return 'AVAILABLE';
        const todayStr = new Date().toLocaleDateString('en-CA');
        const expiryStr = coupon.expiryDate.split('T')[0];
        if (expiryStr < todayStr) return 'EXPIRED';

        const expiry = new Date(coupon.expiryDate);
        const now = new Date();
        const diffMs = expiry - now;
        const diffDays = Math.ceil(diffMs / (1000 * 60 * 60 * 24));
        if (diffDays <= 7) return 'EXPIRING_SOON';
        return 'AVAILABLE';
    };

    const processedCoupons = coupons
        .filter(c =>
            c.merchant.toLowerCase().includes(search.toLowerCase()) ||
            (c.description && c.description.toLowerCase().includes(search.toLowerCase()))
        )
        .filter(c => {
            if (filter === 'ALL') return true;
            return getStatus(c) === filter;
        })
        .sort((a, b) => {
            const statusA = getStatus(a) === 'EXPIRED' ? 1 : 0;
            const statusB = getStatus(b) === 'EXPIRED' ? 1 : 0;

            if (statusA !== statusB) return statusA - statusB; // Active (0) before Expired (1)

            if (statusA === 1) { // Both Expired
                return new Date(b.expiryDate || 0) - new Date(a.expiryDate || 0); // DESC
            } else { // Both Active
                if (!a.expiryDate) return 1;
                if (!b.expiryDate) return -1;
                return new Date(a.expiryDate) - new Date(b.expiryDate); // ASC
            }
        });
    return (
        <div>
            <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center mb-8 gap-4">
                <div>
                    <h1 className="text-xl md:text-3xl font-bold text-white mb-1">
                        Welcome back, {user?.name ? user.name.split(' ')[0] : 'User'}!
                    </h1>
                    <p className="text-gray-400">Here are your added coupons.</p>
                </div>

                <div className="flex gap-4 w-full sm:w-auto mt-4 sm:mt-0">
                    <div className="relative flex-1 sm:w-64">
                        <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-500" />
                        <input
                            type="text"
                            placeholder="Search merchant..."
                            value={search}
                            onChange={(e) => setSearch(e.target.value)}
                            className="w-full bg-gray-900 border border-gray-800 rounded-lg pl-10 pr-4 py-2 text-white focus:ring-2 focus:ring-indigo-600 outline-none"
                        />
                    </div>
                    <button
                        onClick={() => { setEditingCoupon(null); setIsModalOpen(true); }}
                        className="bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2 rounded-lg flex items-center gap-2 font-medium transition-colors whitespace-nowrap"
                    >
                        <Plus className="w-5 h-5" />
                        Add Coupon
                    </button>
                </div>
            </div>



            <div className="flex gap-2 mb-6 overflow-x-auto pb-2 scrollbar-hide">
                {['ALL', 'EXPIRING_SOON', 'AVAILABLE', 'EXPIRED'].map(f => (
                    <button
                        key={f}
                        onClick={() => setFilter(f)}
                        className={`px-4 py-2 rounded-full text-sm font-medium whitespace-nowrap transition-colors border ${filter === f
                                ? 'bg-indigo-500/10 text-indigo-400 border-indigo-500/50'
                                : 'bg-gray-900 text-gray-400 border-gray-800 hover:bg-gray-800 hover:text-white'
                            }`}
                    >
                        {f.replace('_', ' ')}
                    </button>
                ))}
            </div>

            {
                loading ? (
                    <div className="text-center text-gray-500 py-20">Loading...</div>
                ) : processedCoupons.length === 0 ? (
                    <div className="text-center py-20 bg-gray-900/50 rounded-2xl border border-gray-800 border-dashed">
                        <p className="text-gray-400 text-lg">No coupons found. Add one to get started!</p>
                    </div>
                ) : (
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                        {processedCoupons.map(coupon => (
                            <CouponCard
                                key={coupon.id}
                                coupon={coupon}
                                onDelete={handleDeleteClick}
                                onEdit={handleEdit}
                            />
                        ))}
                    </div>
                )
            }

            <CouponModal
                isOpen={isModalOpen}
                onClose={() => setIsModalOpen(false)}
                onSubmit={handleCreateOrUpdate}
                initialData={editingCoupon}
            />

            <ConfirmationModal
                isOpen={isDeleteModalOpen}
                onClose={() => setIsDeleteModalOpen(false)}
                onConfirm={confirmDelete}
                title="Delete Coupon"
                message="Are you sure you want to delete this coupon? This action cannot be undone."
            />
        </div >
    );
};

export default Dashboard;
