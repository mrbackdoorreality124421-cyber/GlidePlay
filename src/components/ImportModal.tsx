import { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { CheckCircle2, Loader2, FolderArchive, FileSearch, Cpu, Settings, Gamepad2, Layers } from 'lucide-react';
import { useApp } from '../AppContext';
import { Game } from '../types';

const IMPORT_STEPS = [
  { id: 1, text: "Importing ZIP via Storage Access...", icon: FolderArchive },
  { id: 2, text: "Copying to private storage...", icon: Layers },
  { id: 3, text: "Validating ZIP format...", icon: CheckCircle2 },
  { id: 4, text: "Extracting files...", icon: FolderArchive },
  { id: 5, text: "Scanning extracted files...", icon: FileSearch },
  { id: 6, text: "Detecting executables (.exe)...", icon: FileSearch },
  { id: 7, text: "Detecting dependencies (DirectX, VC++)...", icon: Layers },
  { id: 8, text: "Estimating game weight...", icon: Cpu },
  { id: 9, text: "Checking device hardware...", icon: Cpu },
  { id: 10, text: "Creating recommended profile...", icon: Settings },
  { id: 11, text: "Configuring controls template...", icon: Gamepad2 },
  { id: 12, text: "Adding to library...", icon: CheckCircle2 },
];

export function ImportModal() {
  const { state, dispatch } = useApp();
  const [currentStep, setCurrentStep] = useState(0);

  useEffect(() => {
    if (!state.isImporting) return;
    
    setCurrentStep(0);
    
    let step = 0;
    const interval = setInterval(() => {
      step++;
      if (step < IMPORT_STEPS.length) {
        setCurrentStep(step);
      } else {
        clearInterval(interval);
        setTimeout(() => {
          // Add a new mock game based on the import
          const newGame: Game = {
            id: Date.now().toString(),
            name: "Grand Theft Auto: San Andreas",
            status: 'Ready',
            profile: 'Ultra',
            weight: 'Heavy',
            size: '4.7 GB'
          };
          dispatch({ type: 'ADD_GAME', payload: newGame });
          dispatch({ type: 'SET_IMPORTING', payload: false });
        }, 1000);
      }
    }, 800); // 800ms per step for realism

    return () => clearInterval(interval);
  }, [state.isImporting, dispatch]);

  if (!state.isImporting) return null;

  const currentStepData = IMPORT_STEPS[currentStep] || IMPORT_STEPS[IMPORT_STEPS.length - 1];
  const CurrentIcon = currentStepData.icon;
  const progress = ((currentStep + 1) / IMPORT_STEPS.length) * 100;

  return (
    <div className="absolute inset-0 z-50 flex items-center justify-center p-6 bg-zinc-950/80 backdrop-blur-md">
      <motion.div 
        initial={{ opacity: 0, scale: 0.9 }}
        animate={{ opacity: 1, scale: 1 }}
        className="bg-zinc-900 border border-zinc-800 rounded-3xl p-6 w-full max-w-sm shadow-2xl flex flex-col items-center text-center"
      >
        <div className="w-20 h-20 rounded-full bg-emerald-500/10 flex items-center justify-center mb-6 relative">
          <motion.div 
            animate={{ rotate: 360 }} 
            transition={{ repeat: Infinity, duration: 4, ease: "linear" }}
            className="absolute inset-0 rounded-full border-2 border-emerald-500/30 border-t-emerald-500"
          />
          <CurrentIcon className="w-8 h-8 text-emerald-400" />
        </div>
        
        <h3 className="text-xl font-bold text-white mb-2">Setting up Game</h3>
        
        <div className="h-12 flex items-center justify-center">
          <p className="text-zinc-400 text-sm font-medium">{currentStepData.text}</p>
        </div>

        <div className="w-full h-2 bg-zinc-800 rounded-full mt-6 overflow-hidden">
          <motion.div 
            className="h-full bg-emerald-500"
            initial={{ width: 0 }}
            animate={{ width: `${progress}%` }}
            transition={{ ease: "easeInOut" }}
          />
        </div>
        <p className="text-zinc-600 text-xs mt-3 font-mono">
          Step {Math.min(currentStep + 1, IMPORT_STEPS.length)} of {IMPORT_STEPS.length}
        </p>
      </motion.div>
    </div>
  );
}
