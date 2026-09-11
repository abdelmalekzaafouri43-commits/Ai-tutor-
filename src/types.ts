export type DifficultyLevel = 'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED';

export type QuestionType = 'MULTIPLE_CHOICE' | 'FILL_IN_BLANK' | 'SHORT_ANSWER';

export interface QuestionItem {
  id: string;
  type: QuestionType;
  questionText: string;
  options: string[];
  correctAnswer: string;
  explanation: string;
  hint: string;
}

export type AppTheme = 
  | 'SAPPHIRE' 
  | 'ARCTIC_BREEZE' 
  | 'PAPER_INK' 
  | 'ALABASTER' 
  | 'NORDIC_FROST' 
  | 'SAGE_SERENITY' 
  | 'LAVENDER_MIST' 
  | 'EARTHY_MOSS';

export type OutputStructure = 
  | 'EXERCISES_ONLY' 
  | 'EXPLANATION_AND_EXERCISES' 
  | 'MULTIPLE_CHOICE_QUIZ';

export type WorksheetTemplate = 
  | 'MIXED_FORMAT' 
  | 'FILL_IN_BLANKS' 
  | 'MULTIPLE_CHOICE' 
  | 'SENTENCE_CORRECTION';

export interface ActiveWorksheet {
  id?: string; // For saved worksheets
  title: string;
  topic: string;
  difficulty: string;
  questions: QuestionItem[];
  rawInput: string;
  targetRules: string;
  isSaved: boolean;
  isFavorite?: boolean;
}

export interface UserSubscription {
  userName: string;
  schoolName: string;
  isPremium: boolean;
  generationsUsedToday: number;
  dailyLimit: number;
}

export interface GrammarTopic {
  id: string;
  title: string;
  category: string;
  description: string;
  difficulty: string;
  sampleSnippet: string;
}
