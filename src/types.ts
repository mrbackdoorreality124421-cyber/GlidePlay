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
