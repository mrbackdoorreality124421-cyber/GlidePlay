import { motion } from 'motion/react';
import { Section } from '../data';
import { CodeViewer } from './CodeViewer';

interface ContentAreaProps {
  section: Section;
}

export function ContentArea({ section }: ContentAreaProps) {
  return (
    <motion.div
      key={section.id}
      initial={{ opacity: 0, y: 10 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.3 }}
      className="max-w-4xl mx-auto w-full pb-24"
    >
      <header className="mb-8 border-b border-zinc-800 pb-6">
        <h1 className="text-3xl font-bold text-zinc-100 tracking-tight mb-2">
          {section.title}
        </h1>
        <p className="text-lg text-zinc-400 leading-relaxed">
          {section.description}
        </p>
      </header>

      {section.content && (
        <div className="max-w-none text-zinc-300 mb-8 whitespace-pre-wrap leading-relaxed text-[15px]">
          {section.content}
        </div>
      )}

      {section.codeBlocks?.map((block, idx) => (
        <CodeViewer key={idx} block={block} />
      ))}
    </motion.div>
  );
}
