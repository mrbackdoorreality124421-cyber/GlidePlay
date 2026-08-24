import { architectureSections, Section } from './sectionsPart1';
import { architectureSectionsPart2 } from './sectionsPart2';
import { architectureSectionsPart3 } from './sectionsPart3';

export type { Section, CodeBlock } from './sectionsPart1';

export const allSections: Section[] = [
  ...architectureSections,
  ...architectureSectionsPart2,
  ...architectureSectionsPart3
];
