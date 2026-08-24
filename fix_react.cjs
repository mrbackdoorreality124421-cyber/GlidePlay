const fs = require('fs');

const contextCode = `
import React, { createContext, useContext, useReducer, ReactNode } from 'react';
import { AppState, Action } from './types';

const initialState: AppState = {
  activeTab: 'library',
  isImporting: false,
  games: [],
  selectedGame: null,
  isPlaying: false,
  settings: {
    autoOptimize: true,
    thermalProtection: true,
    performanceMonitor: false
  }
};

function appReducer(state: AppState, action: Action): AppState {
  switch (action.type) {
    case 'SET_TAB':
      return { ...state, activeTab: action.payload };
    case 'SET_IMPORTING':
      return { ...state, isImporting: action.payload };
    case 'ADD_GAME':
      return { ...state, games: [...state.games, action.payload] };
    case 'SELECT_GAME':
      return { ...state, selectedGame: action.payload };
    case 'SET_PLAYING':
      return { ...state, isPlaying: action.payload };
    case 'TOGGLE_SETTING':
      return { ...state, settings: { ...state.settings, [action.payload]: !state.settings[action.payload] } };
    default:
      return state;
  }
}

const AppContext = createContext<{
  state: AppState;
  dispatch: React.Dispatch<Action>;
} | undefined>(undefined);

export function AppProvider({ children }: { children: ReactNode }) {
  const [state, dispatch] = useReducer(appReducer, initialState);
  return (
    <AppContext.Provider value={{ state, dispatch }}>
      {children}
    </AppContext.Provider>
  );
}

export function useApp() {
  const context = useContext(AppContext);
  if (context === undefined) {
    throw new Error('useApp must be used within an AppProvider');
  }
  return context;
}
`;

fs.writeFileSync('src/AppContext.tsx', contextCode.trim() + '\n');

const typesCode = `
export type Tab = 'library' | 'optimizer' | 'controls' | 'settings';

export interface Game {
  id: string;
  name: string;
  status: string;
  profile: string;
  weight: string;
  size: string;
}

export interface AppState {
  activeTab: Tab;
  isImporting: boolean;
  games: Game[];
  selectedGame: Game | null;
  isPlaying: boolean;
  settings: any;
}

export type Action =
  | { type: 'SET_TAB'; payload: Tab }
  | { type: 'SET_IMPORTING'; payload: boolean }
  | { type: 'ADD_GAME'; payload: Game }
  | { type: 'SELECT_GAME'; payload: Game | null }
  | { type: 'SET_PLAYING'; payload: boolean }
  | { type: 'TOGGLE_SETTING'; payload: string };
`;
fs.writeFileSync('src/types.ts', typesCode.trim() + '\n');

const settingsCode = `
import React from "react";
import { motion } from 'motion/react';
import { Moon, Cpu, FolderOpen, Shield, Gamepad, Info, HardDrive } from 'lucide-react';
import { useApp } from '../AppContext';

export function SettingsScreen() {
  const { state, dispatch } = useApp();
  return (
    <div className="h-full flex flex-col pt-12 px-6 pb-24 overflow-y-auto custom-scrollbar">
      <h2 className="text-3xl font-bold text-white mb-2">Settings</h2>
      <p className="text-zinc-400 mb-8">Configure SmoothPlay behavior.</p>
      <div className="flex flex-col gap-6">
        <SettingsGroup title="System & Performance">
          <ToggleRow icon={Cpu} label="Auto Optimize Games" settingKey="autoOptimize" desc="Adjust resolution dynamically" active={state.settings.autoOptimize} />
          <ToggleRow icon={Shield} label="Thermal Protection" settingKey="thermalProtection" desc="Prevent overheating" active={state.settings.thermalProtection} />
          <ToggleRow icon={Moon} label="Performance Monitor" settingKey="performanceMonitor" desc="Show FPS and stats in-game" active={state.settings.performanceMonitor} />
        </SettingsGroup>
        <SettingsGroup title="Storage & Data">
          <ActionRow icon={FolderOpen} label="Game Installation Path" value="/Android/data/.../files" />
          <ActionRow icon={HardDrive} label="Clear Shader Cache" value="142 MB" />
        </SettingsGroup>
      </div>
    </div>
  );
}

function SettingsGroup({ title, children }: { title: string, children: React.ReactNode }) {
  return (
    <div>
      <h3 className="text-sm font-bold text-zinc-400 uppercase tracking-wider mb-3 ml-2">{title}</h3>
      <div className="bg-zinc-900 border border-zinc-800 rounded-3xl overflow-hidden flex flex-col">
        {children}
      </div>
    </div>
  );
}

function ToggleRow({ icon: Icon, label, desc, active, settingKey }: any) {
  const { dispatch } = useApp();
  return (
    <div className="flex items-center justify-between p-4 border-b border-zinc-800/50 last:border-0" onClick={() => dispatch({type: 'TOGGLE_SETTING', payload: settingKey})}>
      <div className="flex items-center gap-3">
        <Icon className="w-5 h-5 text-zinc-400" />
        <div>
          <span className="font-medium text-zinc-200 block">{label}</span>
          <span className="text-xs text-zinc-500 block">{desc}</span>
        </div>
      </div>
      <div className={\`w-12 h-6 rounded-full p-1 flex cursor-pointer transition-colors \${active ? 'bg-emerald-500 justify-end' : 'bg-zinc-700 justify-start'}\`}>
        <motion.div layout className="w-4 h-4 bg-white rounded-full shadow-md" />
      </div>
    </div>
  );
}

function ActionRow({ icon: Icon, label, value }: any) {
  return (
    <button className="w-full flex items-center justify-between p-4 border-b border-zinc-800/50 last:border-0 active:bg-zinc-800 transition-colors text-left">
      <div className="flex items-center gap-3">
        <Icon className="w-5 h-5 text-zinc-400" />
        <span className="font-medium text-zinc-200">{label}</span>
      </div>
      <span className="text-sm font-medium text-zinc-500 truncate max-w-[100px]">{value}</span>
    </button>
  );
}
`;
fs.writeFileSync('src/screens/SettingsScreen.tsx', settingsCode.trim() + '\n');
