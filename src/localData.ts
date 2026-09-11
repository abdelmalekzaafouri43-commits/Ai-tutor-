import { GrammarTopic, AppTheme, QuestionItem } from './types';

export const GRAMMAR_TOPICS: GrammarTopic[] = [
  {
    id: 'vt',
    title: 'Verb Tenses',
    category: 'Tenses',
    description: 'Mastering Simple, Continuous, Perfect, and Perfect Continuous forms.',
    difficulty: 'Intermediate',
    sampleSnippet: 'She had been studying English literature for three years before she applied to Oxford.'
  },
  {
    id: 'pv',
    title: 'Passive Voice',
    category: 'Syntax',
    description: 'Transforming active sentences into passive for professional, objective tone.',
    difficulty: 'Intermediate',
    sampleSnippet: 'The auditor reviewed the files. -> All files were reviewed by the auditor.'
  },
  {
    id: 'rs',
    title: 'Reported Speech',
    category: 'Syntax',
    description: 'Converting direct speech into indirect with proper pronoun and tense shifting.',
    difficulty: 'Advanced',
    sampleSnippet: 'He said, "I will call you tomorrow." -> He said that he would call me the next day.'
  },
  {
    id: 'sc',
    title: 'Subjunctive Mood',
    category: 'Mood',
    description: 'Expressing wishes, hypothetical situations, or urgent recommendations.',
    difficulty: 'Advanced',
    sampleSnippet: 'It is vital that he submit the grammar report before the meeting starts.'
  },
  {
    id: 'rc',
    title: 'Relative Clauses',
    category: 'Clauses',
    description: 'Using who, whom, whose, which, and that to combine sentences gracefully.',
    difficulty: 'Beginner',
    sampleSnippet: 'The teacher who coordinates the ESL syllabus is highly qualified.'
  },
  {
    id: 'cn',
    title: 'Conditionals',
    category: 'Clauses',
    description: 'Constructing Zero, First, Second, Third, and Mixed conditional sentences.',
    difficulty: 'Advanced',
    sampleSnippet: 'If she had practiced more regularly, she would have completed the worksheet easily.'
  }
];

export const THEMES: { id: AppTheme; name: string; primary: string; secondary: string; darkBackground: string }[] = [
  { id: 'SAPPHIRE', name: 'Sapphire Academic', primary: 'bg-indigo-600 dark:bg-indigo-500', secondary: 'bg-indigo-50 dark:bg-indigo-950/40', darkBackground: 'dark:bg-slate-900' },
  { id: 'ARCTIC_BREEZE', name: 'Arctic Breeze', primary: 'bg-cyan-600 dark:bg-cyan-500', secondary: 'bg-cyan-50 dark:bg-cyan-950/40', darkBackground: 'dark:bg-zinc-900' },
  { id: 'PAPER_INK', name: 'Paper & Ink', primary: 'bg-slate-800 dark:bg-slate-200', secondary: 'bg-slate-100 dark:bg-slate-800/60', darkBackground: 'dark:bg-stone-900' },
  { id: 'ALABASTER', name: 'Alabaster Chalk', primary: 'bg-stone-700 dark:bg-stone-300', secondary: 'bg-stone-100 dark:bg-stone-800/60', darkBackground: 'dark:bg-neutral-900' },
  { id: 'NORDIC_FROST', name: 'Nordic Frost', primary: 'bg-blue-700 dark:bg-blue-400', secondary: 'bg-blue-50 dark:bg-blue-950/30', darkBackground: 'dark:bg-slate-950' },
  { id: 'SAGE_SERENITY', name: 'Sage Serenity', primary: 'bg-emerald-700 dark:bg-emerald-500', secondary: 'bg-emerald-50 dark:bg-emerald-950/40', darkBackground: 'dark:bg-zinc-950' },
  { id: 'LAVENDER_MIST', name: 'Lavender Mist', primary: 'bg-purple-600 dark:bg-purple-500', secondary: 'bg-purple-50 dark:bg-purple-950/40', darkBackground: 'dark:bg-neutral-950' },
  { id: 'EARTHY_MOSS', name: 'Earthy Moss', primary: 'bg-amber-800 dark:bg-amber-600', secondary: 'bg-amber-50 dark:bg-amber-950/30', darkBackground: 'dark:bg-slate-900' }
];

export const MOCK_QUESTIONS: Record<string, QuestionItem[]> = {
  'Verb Tenses': [
    {
      id: 'vt_1',
      type: 'MULTIPLE_CHOICE',
      questionText: 'By the time the bell rang, the students _______ their grammar exam.',
      options: [
        'A) finished',
        'B) had finished',
        'C) have finished',
        'D) finish'
      ],
      correctAnswer: 'B) had finished',
      explanation: 'The Past Perfect ("had finished") is used for an action completed BEFORE another past time event ("bell rang").',
      hint: 'Look for the time marker "By the time..." referencing a past event.'
    },
    {
      id: 'vt_2',
      type: 'FILL_IN_BLANK',
      questionText: 'She _______ (study) English literature for three years before she applied to Oxford.',
      options: [],
      correctAnswer: 'had been studying',
      explanation: 'Past Perfect Continuous ("had been studying") shows an ongoing action that continued up to another point in the past.',
      hint: 'The action was ongoing before the moment of applying.'
    },
    {
      id: 'vt_3',
      type: 'SHORT_ANSWER',
      questionText: 'Rewrite this sentence in the Present Perfect Continuous: "They worked on the grammar lesson since morning."',
      options: [],
      correctAnswer: 'They have been working on the grammar lesson since morning.',
      explanation: 'Present Perfect Continuous structure is: subject + have/has been + verb-ing.',
      hint: 'Use have/has been and the -ing form of work.'
    },
    {
      id: 'vt_4',
      type: 'MULTIPLE_CHOICE',
      questionText: 'If you _______ tomorrow, I will show you our new educational worksheets.',
      options: [
        'A) will visit',
        'B) visited',
        'C) visit',
        'D) had visited'
      ],
      correctAnswer: 'C) visit',
      explanation: 'In the First Conditional, use Simple Present in the "if" clause and future simple in the result clause.',
      hint: 'The result clause has "will show", so use present simple for the condition.'
    }
  ],
  'Passive Voice': [
    {
      id: 'pv_1',
      type: 'MULTIPLE_CHOICE',
      questionText: 'Active: "The tutor will grade the sheets tonight." -> Passive:',
      options: [
        'A) The sheets will be graded by the tutor tonight.',
        'B) The sheets will graded by the tutor tonight.',
        'C) The sheets are graded by the tutor tonight.',
        'D) The sheets would be graded by the tutor tonight.'
      ],
      correctAnswer: 'A) The sheets will be graded by the tutor tonight.',
      explanation: 'The future simple active ("will grade") becomes "will be graded" in the passive voice.',
      hint: 'Keep the future simple auxiliary "will" and add "be" + past participle.'
    },
    {
      id: 'pv_2',
      type: 'FILL_IN_BLANK',
      questionText: 'All grammar errors _______ (correct) by the teacher yesterday before printing.',
      options: [],
      correctAnswer: 'were corrected',
      explanation: 'Since "Yesterday" indicates simple past and "errors" is plural passive, we use "were corrected".',
      hint: 'Simple past passive for plural subjects.'
    },
    {
      id: 'pv_3',
      type: 'SHORT_ANSWER',
      questionText: 'Convert this active sentence to passive: "The student solved all grammar questions easily."',
      options: [],
      correctAnswer: 'All grammar questions were solved easily by the student.',
      explanation: 'The object "All grammar questions" becomes subject, "solved" becomes "were solved", and "by the student" is appended.',
      hint: 'Start with "All grammar questions" and use the past passive.'
    },
    {
      id: 'pv_4',
      type: 'MULTIPLE_CHOICE',
      questionText: 'Identify the grammatically correct passive form of: "Someone has stolen my homework."',
      options: [
        'A) My homework was stolen.',
        'B) My homework has stolen.',
        'C) My homework has been stolen.',
        'D) My homework is being stolen.'
      ],
      correctAnswer: 'C) My homework has been stolen.',
      explanation: 'Present perfect active ("has stolen") becomes "has been stolen" in the passive.',
      hint: 'Use the present perfect auxiliary "has been" + past participle.'
    }
  ]
};
