import { motion } from 'motion/react';
import { X, Activity, Battery, Cpu, Wifi } from 'lucide-react';
import { useApp } from '../AppContext';
import { useEffect, useState } from 'react';

export function GameplaySimulation() {
  const { state, dispatch } = useApp();
  const [fps, setFps] = useState(60);

  useEffect(() => {
    if (!state.isPlaying) return;
    
    // Simulate fluctuating FPS
    const interval = setInterval(() => {
      setFps(prev => {
        const target = state.selectedGame?.weight === 'Heavy' ? 45 : 60;
        const fluctuation = Math.floor(Math.random() * 6) - 3; // -3 to +3
        return Math.min(60, Math.max(20, target + fluctuation));
      });
    }, 1000);

    return () => clearInterval(interval);
  }, [state.isPlaying, state.selectedGame]);

  if (!state.isPlaying || !state.selectedGame) return null;

  return (
    <motion.div 
      initial={{ opacity: 0, scale: 0.95 }}
      animate={{ opacity: 1, scale: 1 }}
      exit={{ opacity: 0, scale: 1.05 }}
      className="absolute inset-0 z-50 bg-black flex items-center justify-center overflow-hidden"
    >
      {/* Fake Game Environment (Background) */}
      <div className="absolute inset-0 bg-[url('https://images.unsplash.com/photo-1542751371-adc38448a05e?q=80&w=2070&auto=format&fit=crop')] bg-cover bg-center opacity-40 blur-[2px] scale-105" />
      <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-transparent to-black/30" />

      {/* Top Status Bar (Like a native phone in landscape) */}
      <div className="absolute top-2 left-4 right-4 flex justify-between items-start z-20">
        <div className="flex gap-2">
          {/* Performance Monitor Overlay */}
          <div className="bg-black/60 backdrop-blur-md rounded-xl p-2 border border-white/10 font-mono text-[10px] text-emerald-400 flex flex-col gap-1 w-32 shadow-xl">
            <div className="flex justify-between items-center text-xs">
              <span className="text-white font-bold">FPS</span>
              <span className={fps >= 50 ? 'text-emerald-400' : fps >= 30 ? 'text-amber-400' : 'text-red-400'}>
                {fps}
              </span>
            </div>
            <div className="h-[1px] bg-white/20 w-full my-0.5" />
            <div className="flex justify-between"><span>CPU</span><span>78%</span></div>
            <div className="flex justify-between"><span>GPU</span><span>92%</span></div>
            <div className="flex justify-between"><span>RAM</span><span>3.8GB</span></div>
            <div className="flex justify-between text-amber-400"><span>TEMP</span><span>42°C</span></div>
          </div>
        </div>

        {/* Exit Button */}
        <button 
          onClick={() => dispatch({ type: 'SET_PLAYING', payload: false })}
          className="w-10 h-10 rounded-full bg-black/50 backdrop-blur-md flex items-center justify-center text-white hover:bg-red-500/80 transition-colors border border-white/10"
        >
          <X className="w-5 h-5" />
        </button>
      </div>

      {/* Center "Game" Content for simulation sake */}
      <div className="relative z-10 flex flex-col items-center">
        <h1 className="text-4xl md:text-6xl font-black text-white tracking-tighter drop-shadow-2xl italic">
          {state.selectedGame.name}
        </h1>
        <p className="text-white/60 font-mono mt-4 animate-pulse">Running via Box64 + DXVK Translator</p>
      </div>

      {/* On-Screen Controls Simulation (Similar to controls editor but active) */}
      <div className="absolute bottom-8 left-12 w-24 h-24 rounded-full border-2 border-white/10 bg-white/5 flex items-center justify-center backdrop-blur-sm z-20">
        <div className="w-10 h-10 rounded-full bg-white/20 shadow-lg" />
      </div>

      <div className="absolute bottom-12 right-12 flex gap-4 z-20">
        <div className="w-14 h-14 rounded-full border border-white/20 bg-white/10 backdrop-blur-sm flex items-center justify-center mt-10 active:bg-white/30" />
        <div className="w-16 h-16 rounded-full border border-white/20 bg-emerald-500/20 backdrop-blur-sm flex items-center justify-center shadow-[0_0_20px_rgba(16,185,129,0.3)] active:bg-emerald-500/40" />
      </div>
    </motion.div>
  );
}
