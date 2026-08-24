import { AnimatePresence } from 'motion/react';
import { useApp, AppProvider } from './AppContext';
import { BottomNav } from './components/BottomNav';
import { LibraryScreen } from './screens/LibraryScreen';
import { OptimizerScreen } from './screens/OptimizerScreen';
import { ControlsScreen } from './screens/ControlsScreen';
import { SettingsScreen } from './screens/SettingsScreen';
import { ImportModal } from './components/ImportModal';
import { GameDetailScreen } from './screens/GameDetailScreen';
import { GameplaySimulation } from './components/GameplaySimulation';

function AppContent() {
  const { state } = useApp();

  return (
    <div className="flex justify-center items-center min-h-screen bg-black">
      {/* Mobile Frame Container */}
      <div className="w-full h-[100dvh] md:h-[90vh] md:max-w-[420px] md:rounded-[40px] bg-zinc-950 text-white relative overflow-hidden shadow-2xl md:border-[8px] md:border-zinc-900 flex flex-col font-sans">
        
        {/* Main View Area */}
        <div className="flex-1 relative overflow-hidden">
          {state.activeTab === 'library' && <LibraryScreen />}
          {state.activeTab === 'optimizer' && <OptimizerScreen />}
          {state.activeTab === 'controls' && <ControlsScreen />}
          {state.activeTab === 'settings' && <SettingsScreen />}
        </div>

        {/* Overlays & Modals */}
        <AnimatePresence>
          {state.selectedGame && !state.isPlaying && <GameDetailScreen />}
          {state.isPlaying && <GameplaySimulation />}
        </AnimatePresence>

        <ImportModal />

        {/* Bottom Navigation (Hides when playing game or in detail screen) */}
        {!state.selectedGame && !state.isPlaying && <BottomNav />}
      </div>
    </div>
  );
}

export default function App() {
  return (
    <AppProvider>
      <AppContent />
    </AppProvider>
  );
}
