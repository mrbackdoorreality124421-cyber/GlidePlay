import React from "react";
import { useState, useRef } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { Upload, FileArchive, Settings, Play, Trash2, Cpu, HardDrive } from 'lucide-react';
import { useApp } from '../AppContext';
import { Game } from '../types';

export function LibraryScreen() {
  const { state, dispatch } = useApp();
  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleAddZip = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      dispatch({ type: 'SET_IMPORTING', payload: true });
      // We simulate the import process, so we reset the input
      if (fileInputRef.current) fileInputRef.current.value = '';
    }
  };

  return (
    <div className="h-full flex flex-col pt-6 px-4 pb-24 overflow-y-auto custom-scrollbar">
      <div className="flex items-center gap-3 mb-8">
        <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-emerald-400 to-emerald-600 flex items-center justify-center shadow-lg shadow-emerald-500/20">
          <Gamepad2 className="w-6 h-6 text-zinc-950" strokeWidth={2.5} />
        </div>
        <h1 className="text-2xl font-bold tracking-tight text-white">SmoothPlay</h1>
      </div>

      {/* Massive Add Button */}
      <div 
        onClick={() => fileInputRef.current?.click()}
        className="relative overflow-hidden group cursor-pointer w-full bg-emerald-500 hover:bg-emerald-400 active:scale-[0.98] transition-all duration-300 rounded-3xl p-6 flex flex-col items-center justify-center min-h-[140px] shadow-xl shadow-emerald-900/20 mb-8 border border-emerald-400/50"
      >
        <div className="absolute inset-0 bg-[url('https://www.transparenttextures.com/patterns/carbon-fibre.png')] opacity-10 mix-blend-overlay"></div>
        <Upload className="w-10 h-10 text-emerald-950 mb-3" />
        <h2 className="text-xl font-bold text-emerald-950">Add Game ZIP</h2>
        <p className="text-emerald-900/80 text-sm font-medium mt-1">Tap or Drag & Drop</p>
        <input 
          type="file" 
          ref={fileInputRef} 
          accept=".zip,.rar,.7z" 
          className="hidden" 
          onChange={handleAddZip}
        />
      </div>

      <h3 className="text-lg font-bold text-zinc-100 mb-4 flex items-center justify-between">
        <span>Your Library</span>
        <span className="text-sm font-medium text-zinc-500 bg-zinc-900 px-3 py-1 rounded-full">{state.games.length} Games</span>
      </h3>

      <div className="grid grid-cols-1 gap-4">
        <AnimatePresence>
          {state.games.length === 0 ? (
            <motion.div 
              initial={{ opacity: 0 }} animate={{ opacity: 1 }} 
              className="text-center py-12 px-6 border-2 border-dashed border-zinc-800 rounded-3xl"
            >
              <FileArchive className="w-12 h-12 text-zinc-700 mx-auto mb-3" />
              <p className="text-zinc-500 font-medium">No games yet. Add a ZIP file to get started.</p>
            </motion.div>
          ) : (
            state.games.map((game) => (
              <GameCard key={game.id} game={game} />
            ))
          )}
        </AnimatePresence>
      </div>
    </div>
  );
}

function GameCard({ game, key }: { game: Game, key?: any }) {
  const { dispatch } = useApp();

  return (
    <motion.div
      layout
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      exit={{ opacity: 0, scale: 0.9 }}
      onClick={() => dispatch({ type: 'SELECT_GAME', payload: game })}
      className="bg-zinc-900/80 backdrop-blur border border-zinc-800 rounded-2xl p-4 flex gap-4 items-center active:scale-[0.98] transition-transform cursor-pointer hover:bg-zinc-800/80"
    >
      <div className="w-20 h-20 rounded-xl bg-zinc-800 flex items-center justify-center flex-shrink-0 relative overflow-hidden border border-zinc-700">
        <Gamepad2 className="w-8 h-8 text-zinc-600" />
      </div>
      
      <div className="flex-1 min-w-0">
        <h4 className="font-bold text-zinc-100 text-lg truncate">{game.name}</h4>
        
        <div className="flex flex-wrap gap-2 mt-2">
          <span className={`text-[10px] font-bold uppercase tracking-wider px-2 py-1 rounded-md ${
            game.status === 'Ready' ? 'bg-emerald-500/10 text-emerald-400' : 'bg-amber-500/10 text-amber-400'
          }`}>
            {game.status}
          </span>
          <span className="text-[10px] font-bold uppercase tracking-wider px-2 py-1 rounded-md bg-zinc-800 text-zinc-400 flex items-center gap-1">
            <Settings className="w-3 h-3" /> {game.profile}
          </span>
        </div>
      </div>

      <button 
        onClick={(e) => {
          e.stopPropagation();
          dispatch({ type: 'SET_PLAYING', payload: true });
        }}
        className="w-12 h-12 rounded-full bg-emerald-500 flex items-center justify-center flex-shrink-0 text-emerald-950 hover:bg-emerald-400 active:scale-95 transition-all shadow-lg shadow-emerald-500/20"
      >
        <Play className="w-5 h-5 ml-1" fill="currentColor" />
      </button>
    </motion.div>
  );
}

// Re-importing Gamepad2 here to avoid missing import if it was only in BottomNav
import { Gamepad2 } from 'lucide-react';
