import { CodeBlock } from '../data';
import { ReactNode } from 'react';

interface CodeViewerProps {
  block: CodeBlock;
  key?: string | number;
}

export function CodeViewer({ block }: CodeViewerProps) {
  return (
    <div className="my-6 rounded-lg overflow-hidden border border-zinc-800 bg-[#0d1117] shadow-xl">
      {block.title && (
        <div className="flex items-center justify-between px-4 py-2 bg-[#161b22] border-b border-zinc-800">
          <span className="text-xs font-mono text-zinc-400">{block.title}</span>
          <span className="text-xs font-mono text-zinc-600 uppercase">{block.language}</span>
        </div>
      )}
      <div className="p-4 overflow-x-auto">
        <pre className="text-sm font-mono leading-relaxed text-zinc-300 whitespace-pre">
          <code>{block.code}</code>
        </pre>
      </div>
    </div>
  );
}
