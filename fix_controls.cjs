const fs = require('fs');

const controlsCode = `
import React, { useState } from 'react';
import { motion } from 'motion/react';
import { MousePointer2, Gamepad2, Grid2X2, Move, Undo2, Settings } from 'lucide-react';
import { useApp } from '../AppContext';

export function ControlsScreen() {
  const [activeTemplate, setActiveTemplate] = useState('fps');
  
  // Basic draggable button state
  const [buttons, setButtons] = useState([
    { id: 1, x: 280, y: 150, label: "L-Click" },
    { id: 2, x: 220, y: 220, label: "R-Click" },
    { id: 3, x: 280, y: 220, label: "Space" }
  ]);
  const [joystickPos, setJoystickPos] = useState({ x: 40, y: 150 });

  return (
    <div className="h-full flex flex-col pt-12 pb-24 overflow-hidden">
      <div className="px-6 mb-4">
        <h2 className="text-3xl font-bold text-white mb-2">Controls</h2>
        <p className="text-zinc-400">Map touch inputs to PC binds.</p>
      </div>

      {/* Template Selector */}
      <div className="px-6 mb-6">
        <div className="flex gap-2 overflow-x-auto pb-2 custom-scrollbar">
          {['fps', 'racing', 'rpg', 'custom'].map((t) => (
            <button
              key={t}
              onClick={() => setActiveTemplate(t)}
              className={\`px-4 py-2 rounded-xl text-sm font-bold capitalize whitespace-nowrap transition-all \${
                activeTemplate === t 
                ? 'bg-emerald-500 text-zinc-950' 
                : 'bg-zinc-900 text-zinc-400 hover:text-zinc-200'
              }\`}
            >
              {t}
            </button>
          ))}
        </div>
      </div>

      {/* Editor Canvas Container */}
      <div className="flex-1 mx-6 bg-zinc-900 border border-zinc-800 rounded-3xl relative overflow-hidden flex flex-col">
        {/* Canvas Header */}
        <div className="flex items-center justify-between p-4 border-b border-zinc-800/80 bg-zinc-900/50">
          <div className="flex items-center gap-2">
            <MousePointer2 className="w-4 h-4 text-emerald-400" />
            <span className="text-xs font-bold text-zinc-300 tracking-wider">LAYOUT EDITOR</span>
          </div>
          <button className="text-xs bg-zinc-800 hover:bg-zinc-700 text-white px-3 py-1.5 rounded-lg transition-colors font-medium">
            Save
          </button>
        </div>

        {/* The Draggable Area */}
        <div className="flex-1 relative bg-zinc-950">
          {/* Grid Background */}
          <div className="absolute inset-0 opacity-[0.03]" 
               style={{ backgroundImage: 'radial-gradient(circle at 2px 2px, white 1px, transparent 0)', backgroundSize: '24px 24px' }} 
          />

          {/* Virtual Joystick */}
          <motion.div 
            drag
            dragMomentum={false}
            dragElastic={0}
            animate={joystickPos}
            onDragEnd={(e, info) => {
               setJoystickPos({ x: joystickPos.x + info.offset.x, y: joystickPos.y + info.offset.y });
            }}
            className="absolute w-24 h-24 rounded-full border-2 border-white/20 bg-white/5 flex items-center justify-center cursor-move"
          >
            <div className="w-10 h-10 rounded-full bg-white/20 backdrop-blur-sm" />
            <div className="absolute -bottom-6 text-[9px] font-mono text-zinc-500 font-bold tracking-widest">WASD</div>
          </motion.div>

          {/* Virtual Buttons */}
          {buttons.map((btn, idx) => (
             <motion.div
               key={btn.id}
               drag
               dragMomentum={false}
               dragElastic={0}
               initial={{ x: btn.x, y: btn.y }}
               onDragEnd={(e, info) => {
                 const newBtns = [...buttons];
                 newBtns[idx].x += info.offset.x;
                 newBtns[idx].y += info.offset.y;
                 setButtons(newBtns);
               }}
               className="absolute w-12 h-12 rounded-full bg-emerald-500/20 border border-emerald-500/50 flex items-center justify-center cursor-move"
             >
               <span className="text-[10px] font-bold text-emerald-400">{btn.label}</span>
             </motion.div>
          ))}
        </div>
      </div>
    </div>
  );
}
`;
fs.writeFileSync('src/screens/ControlsScreen.tsx', controlsCode.trim() + '\n');
