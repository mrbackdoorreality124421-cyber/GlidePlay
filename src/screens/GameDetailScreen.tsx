import { motion } from 'motion/react';
import { ArrowLeft, Play, Settings2, Trash2, ShieldAlert, Cpu, HardDrive, Zap, RefreshCw } from 'lucide-react';
import { useApp } from '../AppContext';

export function GameDetailScreen() {
  const { state, dispatch } = useApp();
  const game = state.selectedGame;

  if (!game) return null;

  return (
    <motion.div 
      initial={{ opacity: 0, x: 50 }}
      animate={{ opacity: 1, x: 0 }}
      exit={{ opacity: 0, x: 50 }}
      className="absolute inset-0 z-30 bg-zinc-950 flex flex-col overflow-y-auto custom-scrollbar"
    >
      {/* Header Image Area */}
      <div className="relative h-64 bg-zinc-900 border-b border-zinc-800 flex-shrink-0">
        <div className="absolute inset-0 bg-gradient-to-t from-zinc-950 via-zinc-950/60 to-transparent z-10" />
        
        {/* Top bar */}
        <div className="absolute top-0 left-0 w-full p-4 flex justify-between items-center z-20">
          <button 
            onClick={() => dispatch({ type: 'SELECT_GAME', payload: null })}
            className="w-10 h-10 rounded-full bg-black/40 backdrop-blur-md flex items-center justify-center text-white active:scale-95 transition-transform border border-white/10"
          >
            <ArrowLeft className="w-5 h-5" />
          </button>
          
          <button 
            onClick={() => {
              dispatch({ type: 'REMOVE_GAME', payload: game.id });
            }}
            className="w-10 h-10 rounded-full bg-red-500/10 backdrop-blur-md flex items-center justify-center text-red-500 active:scale-95 transition-transform border border-red-500/20"
          >
            <Trash2 className="w-5 h-5" />
          </button>
        </div>

        {/* Title area */}
        <div className="absolute bottom-6 left-6 right-6 z-20">
          <span className="px-2.5 py-1 rounded-md bg-emerald-500 text-emerald-950 text-xs font-bold uppercase tracking-wider mb-3 inline-block">
            {game.status}
          </span>
          <h1 className="text-3xl font-bold text-white leading-tight">{game.name}</h1>
        </div>
      </div>

      <div className="p-6 flex-1 flex flex-col gap-6 pb-24">
        {/* Massive Play Button */}
        <button 
          onClick={() => dispatch({ type: 'SET_PLAYING', payload: true })}
          className="w-full bg-emerald-500 hover:bg-emerald-400 text-emerald-950 rounded-2xl p-4 flex items-center justify-center gap-3 font-bold text-lg active:scale-[0.98] transition-all shadow-lg shadow-emerald-500/20"
        >
          <Play fill="currentColor" className="w-6 h-6" />
          PLAY NOW
        </button>

        {/* Stats Grid */}
        <div className="grid grid-cols-2 gap-4">
          <div className="bg-zinc-900 border border-zinc-800 rounded-2xl p-4 flex flex-col gap-2">
            <div className="flex items-center gap-2 text-zinc-400 mb-1">
              <Zap className="w-4 h-4" />
              <span className="text-xs font-bold uppercase tracking-wider">Profile</span>
            </div>
            <span className="text-lg font-bold text-white">{game.profile}</span>
          </div>
          
          <div className="bg-zinc-900 border border-zinc-800 rounded-2xl p-4 flex flex-col gap-2">
            <div className="flex items-center gap-2 text-zinc-400 mb-1">
              <Cpu className="w-4 h-4" />
              <span className="text-xs font-bold uppercase tracking-wider">Weight</span>
            </div>
            <span className="text-lg font-bold text-white">{game.weight}</span>
          </div>
          
          <div className="bg-zinc-900 border border-zinc-800 rounded-2xl p-4 flex flex-col gap-2">
            <div className="flex items-center gap-2 text-zinc-400 mb-1">
              <HardDrive className="w-4 h-4" />
              <span className="text-xs font-bold uppercase tracking-wider">Storage</span>
            </div>
            <span className="text-lg font-bold text-white">{game.size}</span>
          </div>
          
          <div className="bg-zinc-900 border border-zinc-800 rounded-2xl p-4 flex flex-col gap-2">
            <div className="flex items-center gap-2 text-zinc-400 mb-1">
              <Settings2 className="w-4 h-4" />
              <span className="text-xs font-bold uppercase tracking-wider">Engine</span>
            </div>
            <span className="text-lg font-bold text-white">DXVK + Wine</span>
          </div>
        </div>

        {/* Action List */}
        <div className="bg-zinc-900 border border-zinc-800 rounded-3xl overflow-hidden flex flex-col">
          <ActionRow icon={Settings2} label="Profile Selector" value={game.profile} />
          <div className="h-[1px] w-full bg-zinc-800 ml-12" />
          <ActionRow icon={Gamepad2} label="Controls Editor" value="FPS Template" />
          <div className="h-[1px] w-full bg-zinc-800 ml-12" />
          <ActionRow icon={RefreshCw} label="Install Dependencies" value="DirectX, VC++" />
        </div>

        {/* Warning if heavy */}
        {game.weight === 'Heavy' && (
          <div className="bg-amber-500/10 border border-amber-500/20 rounded-2xl p-4 flex gap-3 items-start">
            <ShieldAlert className="w-5 h-5 text-amber-500 flex-shrink-0 mt-0.5" />
            <p className="text-amber-200/80 text-sm leading-relaxed">
              This game is heavy. Your device may experience thermal throttling during extended sessions. The Balance profile is recommended.
            </p>
          </div>
        )}
      </div>
    </motion.div>
  );
}

function ActionRow({ icon: Icon, label, value }: { icon: any, label: string, value: string }) {
  return (
    <button className="flex items-center justify-between p-4 active:bg-zinc-800 transition-colors text-left">
      <div className="flex items-center gap-3">
        <Icon className="w-5 h-5 text-zinc-400" />
        <span className="font-medium text-zinc-200">{label}</span>
      </div>
      <span className="text-sm font-medium text-zinc-500">{value}</span>
    </button>
  );
}

import { Gamepad2 } from 'lucide-react';
