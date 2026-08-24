import { Section } from '../data';
import { Gamepad2, Code2, FolderTree, FileJson, Settings2, Smartphone, TerminalSquare } from 'lucide-react';

interface SidebarProps {
  sections: Section[];
  activeId: number;
  onSelect: (id: number) => void;
}

const getIconForSection = (id: number) => {
  if (id === 1 || id === 17 || id === 18) return <TerminalSquare className="w-4 h-4" />;
  if (id === 2 || id === 7 || id === 9 || id === 10 || id === 11) return <Settings2 className="w-4 h-4" />;
  if (id === 3) return <FolderTree className="w-4 h-4" />;
  if (id === 5 || id === 8) return <Gamepad2 className="w-4 h-4" />;
  if (id === 13 || id === 14) return <FileJson className="w-4 h-4" />;
  if (id === 16) return <Smartphone className="w-4 h-4" />;
  return <Code2 className="w-4 h-4" />;
};

export function Sidebar({ sections, activeId, onSelect }: SidebarProps) {
  return (
    <div className="w-80 flex-shrink-0 bg-zinc-950 border-r border-zinc-800 flex flex-col h-screen overflow-y-auto hidden md:flex">
      <div className="p-6 sticky top-0 bg-zinc-950/90 backdrop-blur-sm border-b border-zinc-800/50 z-10">
        <div className="flex items-center gap-3 mb-1">
          <div className="w-8 h-8 rounded-lg bg-emerald-500/20 flex items-center justify-center border border-emerald-500/30">
            <Gamepad2 className="w-5 h-5 text-emerald-400" />
          </div>
          <h2 className="text-xl font-bold text-zinc-100 tracking-tight">SmoothPlay</h2>
        </div>
        <p className="text-xs text-zinc-500 font-mono ml-11">Architecture Plan</p>
      </div>
      
      <nav className="flex-1 px-3 py-4 space-y-1">
        {sections.map((section) => {
          const isActive = activeId === section.id;
          return (
            <button
              key={section.id}
              onClick={() => onSelect(section.id)}
              className={`w-full flex items-center gap-3 px-3 py-2.5 rounded-md text-left text-sm transition-colors ${
                isActive 
                  ? 'bg-emerald-500/10 text-emerald-400 font-medium' 
                  : 'text-zinc-400 hover:bg-zinc-900 hover:text-zinc-200'
              }`}
            >
              <span className={`${isActive ? 'text-emerald-400' : 'text-zinc-500'}`}>
                {getIconForSection(section.id)}
              </span>
              <span className="truncate">{section.title.replace(/^\d+\.\s*/, '')}</span>
            </button>
          );
        })}
      </nav>
    </div>
  );
}
