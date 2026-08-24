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
