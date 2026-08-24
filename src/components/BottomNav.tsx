import { Gamepad2, Settings2, SlidersHorizontal, LayoutGrid } from 'lucide-react';
import { useApp } from '../AppContext';
import { Tab } from '../types';

export function BottomNav() {
  const { state, dispatch } = useApp();

  const navItems: { id: Tab; label: string; icon: any }[] = [
    { id: 'library', label: 'Library', icon: LayoutGrid },
    { id: 'optimizer', label: 'Optimizer', icon: SlidersHorizontal },
    { id: 'controls', label: 'Controls', icon: Gamepad2 },
    { id: 'settings', label: 'Settings', icon: Settings2 },
  ];

  return (
    <div className="absolute bottom-0 w-full bg-zinc-950/90 backdrop-blur-xl border-t border-zinc-800/80 px-2 py-2 flex justify-between items-center z-40 pb-safe">
      {navItems.map((item) => {
        const isActive = state.activeTab === item.id;
        const Icon = item.icon;
        return (
          <button
            key={item.id}
            onClick={() => dispatch({ type: 'SET_TAB', payload: item.id })}
            className={`flex-1 flex flex-col items-center justify-center gap-1 py-2 px-1 rounded-2xl transition-all duration-300 ${
              isActive ? 'text-emerald-400' : 'text-zinc-500 hover:text-zinc-300'
            }`}
          >
            <div className={`p-1.5 rounded-xl transition-all duration-300 ${isActive ? 'bg-emerald-400/15 scale-110' : 'bg-transparent scale-100'}`}>
              <Icon className="w-6 h-6" strokeWidth={isActive ? 2.5 : 2} />
            </div>
            <span className={`text-[10px] font-medium tracking-wide ${isActive ? 'opacity-100' : 'opacity-80'}`}>
              {item.label}
            </span>
          </button>
        );
      })}
    </div>
  );
}
