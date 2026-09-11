import { useState, useEffect } from 'react';
import { 
  BookOpen, Sparkles, PlusCircle, CheckCircle, FileText, BarChart2, Settings, 
  Moon, Sun, GraduationCap, ArrowRight, Download, Printer, Copy, Check, Info,
  Bookmark, BookmarkCheck, Heart, Trash2, Award, HelpCircle
} from 'lucide-react';
import confetti from 'canvas-confetti';
import { GRAMMAR_TOPICS, THEMES, MOCK_QUESTIONS } from './localData';
import { ActiveWorksheet, AppTheme, UserSubscription, GrammarTopic, OutputStructure, WorksheetTemplate } from './types';

export default function App() {
  // Navigation
  const [activeTab, setActiveTab] = useState<'dashboard' | 'generator' | 'practice' | 'saved' | 'analytics' | 'settings'>('dashboard');

  // Themes & Dark Mode
  const [appTheme, setAppTheme] = useState<AppTheme>('SAPPHIRE');
  const [isDarkMode, setIsDarkMode] = useState<boolean>(false);

  // Settings / Subscription
  const [subscription, setSubscription] = useState<UserSubscription>({
    userName: 'Prof. Sarah Jenkins',
    schoolName: 'Westminster Grammar Academy',
    isPremium: true,
    generationsUsedToday: 2,
    dailyLimit: 5
  });

  // Generator selections
  const [selectedTopic, setSelectedTopic] = useState<GrammarTopic>(GRAMMAR_TOPICS[0]);
  const [difficulty, setDifficulty] = useState<'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED'>('INTERMEDIATE');
  const [includeExplanations, setIncludeExplanations] = useState<boolean>(true);
  const [specificRuleFilter, setSpecificRuleFilter] = useState<string>('');
  const [outputStructure, setOutputStructure] = useState<OutputStructure>('EXPLANATION_AND_EXERCISES');
  const [worksheetTemplate, setWorksheetTemplate] = useState<WorksheetTemplate>('MIXED_FORMAT');
  const [inputMode, setInputMode] = useState<'TOPIC' | 'HTML'>('TOPIC');
  const [rawHtmlInput, setRawHtmlInput] = useState<string>('');

  // Active Worksheet in student mode
  const [activeWorksheet, setActiveWorksheet] = useState<ActiveWorksheet | null>(null);
  const [userAnswers, setUserAnswers] = useState<Record<string, string>>({});
  const [showResults, setShowResults] = useState<boolean>(false);

  // Saved Library Store
  const [savedWorksheets, setSavedWorksheets] = useState<ActiveWorksheet[]>([]);

  // Generation status
  const [isGenerating, setIsGenerating] = useState<boolean>(false);
  const [progressMessage, setProgressMessage] = useState<string>('');

  // Paywall Modal
  const [showPaywall, setShowPaywall] = useState<boolean>(false);
  const [paywallReason, setPaywallReason] = useState<string>('');

  // Copy status indicators
  const [copiedId, setCopiedId] = useState<string | null>(null);

  // Synchronize dark mode class with root HTML element
  useEffect(() => {
    if (isDarkMode) {
      document.documentElement.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
    }
  }, [isDarkMode]);

  // Load from LocalStorage
  useEffect(() => {
    const cached = localStorage.getItem('saved_worksheets');
    if (cached) {
      try {
        setSavedWorksheets(JSON.parse(cached));
      } catch (e) {
        console.error(e);
      }
    } else {
      // Default initial worksheet in library
      const initial: ActiveWorksheet = {
        id: 'ws_demo_1',
        title: 'Advanced Verb Tenses Practice',
        topic: 'Verb Tenses',
        difficulty: 'Intermediate',
        questions: MOCK_QUESTIONS['Verb Tenses'],
        rawInput: 'By the time the bell rang...',
        targetRules: 'Perfect Tenses Shifting',
        isSaved: true,
        isFavorite: true
      };
      setSavedWorksheets([initial]);
      localStorage.setItem('saved_worksheets', JSON.stringify([initial]));
    }

    const cachedTheme = localStorage.getItem('app_theme');
    if (cachedTheme) setAppTheme(cachedTheme as AppTheme);

    const cachedDarkMode = localStorage.getItem('dark_mode');
    if (cachedDarkMode) setIsDarkMode(cachedDarkMode === 'true');

    const cachedSub = localStorage.getItem('user_subscription');
    if (cachedSub) {
      try {
        setSubscription(JSON.parse(cachedSub));
      } catch (e) {
        console.error(e);
      }
    }
  }, []);

  // Save updates to LocalStorage
  const saveWorksheetsToLocalStorage = (list: ActiveWorksheet[]) => {
    setSavedWorksheets(list);
    localStorage.setItem('saved_worksheets', JSON.stringify(list));
  };

  const handleToggleFavorite = (id: string) => {
    const updated = savedWorksheets.map(ws => {
      if (ws.id === id) {
        return { ...ws, isFavorite: !ws.isFavorite };
      }
      return ws;
    });
    saveWorksheetsToLocalStorage(updated);
  };

  const handleDeleteWorksheet = (id: string, e: React.MouseEvent) => {
    e.stopPropagation();
    if (confirm('Are you sure you want to delete this worksheet from your library?')) {
      const updated = savedWorksheets.filter(ws => ws.id !== id);
      saveWorksheetsToLocalStorage(updated);
      if (activeWorksheet && activeWorksheet.id === id) {
        setActiveWorksheet(null);
      }
    }
  };

  // Theme styling helpers
  const currentTheme = THEMES.find(t => t.id === appTheme) || THEMES[0];

  // Helper to copy text to clipboard
  const handleCopyText = (text: string, id: string) => {
    navigator.clipboard.writeText(text);
    setCopiedId(id);
    setTimeout(() => setCopiedId(null), 2000);
  };

  // Simulated AI generator
  const triggerGeneration = () => {
    if (!subscription.isPremium && subscription.generationsUsedToday >= subscription.dailyLimit) {
      setPaywallReason(`You have reached your free daily limit of ${subscription.dailyLimit} generations. Upgrade to Premium for unlimited worksheets!`);
      setShowPaywall(true);
      return;
    }

    setIsGenerating(true);
    setProgressMessage('Connecting to Gemini AI Engine...');

    setTimeout(() => {
      setProgressMessage('Analyzing syntax patterns & extracting vocabulary structures...');
      setTimeout(() => {
        setProgressMessage('Synthesizing questions, hints, & premium explanations...');
        setTimeout(() => {
          // Select mock questions or generate based on topic
          const topicName = inputMode === 'HTML' ? 'HTML Code Analysis' : selectedTopic.title;
          const questions = MOCK_QUESTIONS[selectedTopic.title] || MOCK_QUESTIONS['Verb Tenses'];

          const generatedTitle = inputMode === 'HTML' 
            ? 'Interactive HTML-Derived Grammar Workout' 
            : `${selectedTopic.title} Practice Worksheet`;

          const newWorksheet: ActiveWorksheet = {
            title: generatedTitle,
            topic: topicName,
            difficulty: difficulty.charAt(0) + difficulty.slice(1).toLowerCase(),
            questions: questions.map(q => ({
              ...q,
              id: `${q.id}_gen_${Date.now()}`
            })),
            rawInput: inputMode === 'HTML' ? rawHtmlInput : selectedTopic.sampleSnippet,
            targetRules: specificRuleFilter || 'General Topic Practice',
            isSaved: false
          };

          // Increment generations used
          const updatedSub = {
            ...subscription,
            generationsUsedToday: subscription.generationsUsedToday + 1
          };
          setSubscription(updatedSub);
          localStorage.setItem('user_subscription', JSON.stringify(updatedSub));

          setActiveWorksheet(newWorksheet);
          setUserAnswers({});
          setShowResults(false);
          setIsGenerating(false);
          setActiveTab('practice');
        }, 1000);
      }, 900);
    }, 800);
  };

  // Save current active worksheet to library
  const handleSaveToLibrary = () => {
    if (!activeWorksheet) return;
    if (activeWorksheet.isSaved) return;

    const newSaved: ActiveWorksheet = {
      ...activeWorksheet,
      id: `ws_${Date.now()}`,
      isSaved: true
    };

    const updated = [newSaved, ...savedWorksheets];
    saveWorksheetsToLocalStorage(updated);
    setActiveWorksheet(newSaved);
  };

  // Trigger browser print for pristine PDF output
  const handlePrintWorksheet = () => {
    window.print();
  };

  // Score Calculations
  const calculateScore = () => {
    if (!activeWorksheet) return 0;
    let correct = 0;
    activeWorksheet.questions.forEach(q => {
      const ans = userAnswers[q.id]?.trim().toLowerCase() || '';
      const correctAns = q.correctAnswer.trim().toLowerCase();
      // Handle approximate string match for Short Answers
      if (q.type === 'SHORT_ANSWER') {
        if (correctAns.includes(ans) && ans.length > 3) {
          correct++;
        }
      } else {
        if (ans === correctAns || correctAns.startsWith(ans)) {
          correct++;
        }
      }
    });
    return Math.round((correct / activeWorksheet.questions.length) * 100);
  };

  const handleFinishPractice = () => {
    setShowResults(true);
    const score = calculateScore();
    if (score === 100) {
      confetti({
        particleCount: 150,
        spread: 80,
        origin: { y: 0.6 }
      });
    }
  };

  return (
    <div className="flex min-h-screen bg-slate-50 dark:bg-slate-950 text-slate-800 dark:text-slate-200 antialiased">
      
      {/* 1. LEFT SIDEBAR DASHBOARD NAVIGATION PANEL */}
      <aside className="w-72 bg-white dark:bg-slate-900 border-r border-slate-200 dark:border-slate-800 flex flex-col justify-between shrink-0 no-print">
        <div>
          {/* Header */}
          <div className="p-6 flex items-center gap-3 border-b border-slate-100 dark:border-slate-800">
            <div className={`p-2 rounded-xl text-white ${currentTheme.primary} shadow-md`}>
              <GraduationCap className="w-7 h-7" />
            </div>
            <div>
              <h1 className="font-extrabold text-lg leading-tight tracking-tight text-slate-900 dark:text-white">
                Worksheet Tutor
              </h1>
              <p className="text-xs font-semibold text-indigo-600 dark:text-indigo-400">AI-Powered ESL Hub</p>
            </div>
          </div>

          {/* Nav Items */}
          <nav className="p-4 space-y-1.5">
            <button 
              onClick={() => setActiveTab('dashboard')}
              className={`w-full flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-bold transition-all ${
                activeTab === 'dashboard' 
                  ? `${currentTheme.primary} text-white shadow-sm` 
                  : 'hover:bg-slate-50 dark:hover:bg-slate-800 text-slate-600 dark:text-slate-300'
              }`}
            >
              <BookOpen className="w-5 h-5" />
              <span>Tutor Dashboard</span>
            </button>

            <button 
              onClick={() => setActiveTab('generator')}
              className={`w-full flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-bold transition-all ${
                activeTab === 'generator' 
                  ? `${currentTheme.primary} text-white shadow-sm` 
                  : 'hover:bg-slate-50 dark:hover:bg-slate-800 text-slate-600 dark:text-slate-300'
              }`}
            >
              <PlusCircle className="w-5 h-5" />
              <span>Generate Lesson</span>
            </button>

            {activeWorksheet && (
              <button 
                onClick={() => setActiveTab('practice')}
                className={`w-full flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-bold transition-all ${
                  activeTab === 'practice' 
                    ? `${currentTheme.primary} text-white shadow-sm` 
                    : 'hover:bg-slate-50 dark:hover:bg-slate-800 text-slate-600 dark:text-slate-300'
                }`}
              >
                <CheckCircle className="w-5 h-5" />
                <span>Interactive Practice</span>
              </button>
            )}

            <button 
              onClick={() => setActiveTab('saved')}
              className={`w-full flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-bold transition-all ${
                activeTab === 'saved' 
                  ? `${currentTheme.primary} text-white shadow-sm` 
                  : 'hover:bg-slate-50 dark:hover:bg-slate-800 text-slate-600 dark:text-slate-300'
              }`}
            >
              <FileText className="w-5 h-5" />
              <span>Worksheet Library</span>
            </button>

            <button 
              onClick={() => setActiveTab('analytics')}
              className={`w-full flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-bold transition-all ${
                activeTab === 'analytics' 
                  ? `${currentTheme.primary} text-white shadow-sm` 
                  : 'hover:bg-slate-50 dark:hover:bg-slate-800 text-slate-600 dark:text-slate-300'
              }`}
            >
              <BarChart2 className="w-5 h-5" />
              <span>Metrics & Insights</span>
            </button>

            <button 
              onClick={() => setActiveTab('settings')}
              className={`w-full flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-bold transition-all ${
                activeTab === 'settings' 
                  ? `${currentTheme.primary} text-white shadow-sm` 
                  : 'hover:bg-slate-50 dark:hover:bg-slate-800 text-slate-600 dark:text-slate-300'
              }`}
            >
              <Settings className="w-5 h-5" />
              <span>System Settings</span>
            </button>
          </nav>
        </div>

        {/* Workspace Footer Branding & Subscription */}
        <div className="p-4 border-t border-slate-100 dark:border-slate-800">
          {subscription.isPremium ? (
            <div className="bg-gradient-to-br from-amber-500/10 to-orange-500/10 border border-amber-500/20 rounded-xl p-4">
              <div className="flex items-center gap-2 mb-1">
                <Award className="w-5 h-5 text-amber-500 shrink-0" />
                <span className="font-extrabold text-xs text-amber-700 dark:text-amber-400 tracking-wide uppercase">Educator Premium</span>
              </div>
              <p className="text-[11px] text-slate-500 dark:text-slate-400">Unlimited generations, A4 page margins & branding enabled.</p>
            </div>
          ) : (
            <div className="bg-slate-50 dark:bg-slate-800/50 rounded-xl p-4">
              <div className="flex justify-between text-xs font-bold mb-1.5">
                <span>Free Tier Limit</span>
                <span className="text-indigo-600 dark:text-indigo-400">
                  {subscription.generationsUsedToday}/{subscription.dailyLimit} Daily
                </span>
              </div>
              <div className="w-full bg-slate-200 dark:bg-slate-700 h-2 rounded-full overflow-hidden mb-3">
                <div 
                  className={`h-full ${currentTheme.primary}`} 
                  style={{ width: `${(subscription.generationsUsedToday / subscription.dailyLimit) * 100}%` }}
                />
              </div>
              <button 
                onClick={() => {
                  setPaywallReason('Unlock premium lesson presets and custom branding header designs.');
                  setShowPaywall(true);
                }}
                className={`w-full text-center py-2 rounded-lg text-xs font-bold text-white transition ${currentTheme.primary}`}
              >
                Upgrade to Premium
              </button>
            </div>
          )}

          <div className="mt-4 flex items-center justify-between text-xs text-slate-400 font-medium">
            <span>v2.1 Web App</span>
            <button 
              onClick={() => setIsDarkMode(!isDarkMode)} 
              className="p-1.5 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 text-slate-500 dark:text-slate-300"
            >
              {isDarkMode ? <Sun className="w-4 h-4 text-amber-500" /> : <Moon className="w-4 h-4" />}
            </button>
          </div>
        </div>
      </aside>

      {/* 2. MAIN APPLICATION WORKSPACE CONTENT */}
      <main className="flex-1 min-h-screen overflow-y-auto flex flex-col">
        
        {/* Top bar */}
        <header className="no-print bg-white dark:bg-slate-900 border-b border-slate-200 dark:border-slate-800 px-8 py-4 flex justify-between items-center shrink-0">
          <div>
            <h2 className="text-xl font-extrabold text-slate-900 dark:text-white capitalize">
              {activeTab === 'saved' ? 'Worksheet Library' : activeTab === 'analytics' ? 'Metrics & Analytics' : `${activeTab} workspace`}
            </h2>
            <p className="text-xs font-medium text-slate-400">
              {subscription.schoolName} &bull; {subscription.userName}
            </p>
          </div>

          <div className="flex items-center gap-3">
            <div className="flex flex-col text-right">
              <span className="text-xs font-bold text-slate-900 dark:text-white">{subscription.userName}</span>
              <span className="text-[10px] font-semibold text-slate-400 uppercase tracking-widest">
                {subscription.isPremium ? 'Premium Educator' : 'Free Account'}
              </span>
            </div>
            <div className={`w-10 h-10 rounded-full flex items-center justify-center text-white font-extrabold text-sm ${currentTheme.primary}`}>
              {subscription.userName.charAt(0) || 'E'}
            </div>
          </div>
        </header>

        {/* Screen Container */}
        <div className="flex-1 p-8">
          
          {/* TAB 1: TUTOR DASHBOARD */}
          {activeTab === 'dashboard' && (
            <div className="space-y-8 max-w-5xl">
              {/* Glass AI Tutor Banner */}
              <div className="relative rounded-3xl p-8 overflow-hidden bg-gradient-to-r from-indigo-900 to-indigo-700 text-white shadow-xl border border-indigo-900/50">
                <div className="absolute right-0 bottom-0 top-0 opacity-15 select-none pointer-events-none">
                  <Sparkles className="w-80 h-80 text-white" />
                </div>
                <div className="relative z-10 max-w-2xl space-y-4">
                  <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-white/10 backdrop-blur-md text-xs font-extrabold tracking-wide uppercase text-indigo-200">
                    <Sparkles className="w-3.5 h-3.5" />
                    <span>Next-Gen Gemini Assistant</span>
                  </div>
                  <h3 className="text-3xl font-black tracking-tight leading-none">
                    Generate English Grammar Worksheets instantly.
                  </h3>
                  <p className="text-indigo-100 text-sm leading-relaxed font-medium">
                    Analyze custom HTML code structures, choose from A4 layouts, customize templates, and export worksheets immediately to PDF or Google Docs.
                  </p>
                  <div className="flex gap-4 pt-2">
                    <button 
                      onClick={() => setActiveTab('generator')}
                      className="px-6 py-3 bg-white text-indigo-900 rounded-xl font-bold text-sm hover:shadow-lg hover:scale-[1.02] transition"
                    >
                      Create Custom Worksheet
                    </button>
                    <button 
                      onClick={() => {
                        setSelectedTopic(GRAMMAR_TOPICS[0]);
                        triggerGeneration();
                      }}
                      className="px-6 py-3 bg-indigo-600/50 hover:bg-indigo-600 border border-indigo-400/30 text-white rounded-xl font-bold text-sm transition"
                    >
                      Instant Verb Tenses Demo
                    </button>
                  </div>
                </div>
              </div>

              {/* Grid of quick choices */}
              <div>
                <h4 className="font-extrabold text-lg text-slate-900 dark:text-white mb-4">Select a Topic to Start</h4>
                <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
                  {GRAMMAR_TOPICS.map(topic => (
                    <div 
                      key={topic.id}
                      onClick={() => {
                        setSelectedTopic(topic);
                        setActiveTab('generator');
                      }}
                      className="group bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-6 cursor-pointer hover:border-indigo-500 hover:shadow-md transition-all"
                    >
                      <div className="flex justify-between items-start mb-3">
                        <span className="text-[10px] font-extrabold tracking-wider uppercase px-2 py-0.5 rounded bg-indigo-50 dark:bg-indigo-950/50 text-indigo-600 dark:text-indigo-400">
                          {topic.category}
                        </span>
                        <span className="text-[10px] font-bold text-slate-400">{topic.difficulty}</span>
                      </div>
                      <h5 className="font-extrabold text-slate-900 dark:text-white group-hover:text-indigo-600 dark:group-hover:text-indigo-400 transition-colors mb-1.5">
                        {topic.title}
                      </h5>
                      <p className="text-xs text-slate-500 dark:text-slate-400 leading-relaxed mb-4">
                        {topic.description}
                      </p>
                      <div className="flex items-center gap-1.5 text-xs font-bold text-indigo-600 dark:text-indigo-400">
                        <span>Configure presets</span>
                        <ArrowRight className="w-3.5 h-3.5 transition-transform group-hover:translate-x-1" />
                      </div>
                    </div>
                  ))}
                </div>
              </div>

              {/* Recent Saved Worksheets Library Section */}
              <div>
                <div className="flex justify-between items-center mb-4">
                  <h4 className="font-extrabold text-lg text-slate-900 dark:text-white">Recent Library Worksheets</h4>
                  <button onClick={() => setActiveTab('saved')} className="text-sm font-bold text-indigo-600 dark:text-indigo-400 hover:underline">
                    View Library ({savedWorksheets.length})
                  </button>
                </div>

                {savedWorksheets.length === 0 ? (
                  <div className="bg-white dark:bg-slate-900 border border-dashed border-slate-200 dark:border-slate-800 rounded-2xl p-10 text-center">
                    <p className="text-sm text-slate-500">Your library is currently empty. Generate a lesson first!</p>
                  </div>
                ) : (
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                    {savedWorksheets.slice(0, 2).map(ws => (
                      <div 
                        key={ws.id}
                        onClick={() => {
                          setActiveWorksheet(ws);
                          setUserAnswers({});
                          setShowResults(false);
                          setActiveTab('practice');
                        }}
                        className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-6 cursor-pointer hover:shadow-md hover:border-slate-300 dark:hover:border-slate-700 transition"
                      >
                        <div className="flex justify-between items-start mb-3">
                          <h5 className="font-extrabold text-slate-900 dark:text-white text-base leading-tight">
                            {ws.title}
                          </h5>
                          <button 
                            onClick={(e) => {
                              e.stopPropagation();
                              handleToggleFavorite(ws.id!);
                            }}
                            className="p-1 rounded hover:bg-slate-100 dark:hover:bg-slate-800"
                          >
                            <Heart className={`w-4 h-4 ${ws.isFavorite ? 'fill-red-500 text-red-500' : 'text-slate-400'}`} />
                          </button>
                        </div>
                        <div className="flex gap-4 text-xs font-bold text-slate-400 mb-4">
                          <span>Topic: {ws.topic}</span>
                          <span>&bull;</span>
                          <span>Level: {ws.difficulty}</span>
                        </div>
                        <div className="flex justify-between items-center border-t border-slate-100 dark:border-slate-800 pt-4">
                          <span className="text-xs font-bold text-indigo-600 dark:text-indigo-400">Practice Lesson &rarr;</span>
                          <div className="flex gap-2">
                            <button 
                              onClick={(e) => {
                                e.stopPropagation();
                                window.print();
                              }}
                              className="p-2 rounded bg-slate-50 hover:bg-slate-100 dark:bg-slate-800 dark:hover:bg-slate-700 text-slate-600 dark:text-slate-300"
                              title="Print A4"
                            >
                              <Printer className="w-3.5 h-3.5" />
                            </button>
                            <button 
                              onClick={(e) => handleDeleteWorksheet(ws.id!, e)}
                              className="p-2 rounded bg-red-50 hover:bg-red-100 text-red-600 dark:bg-red-950/20 dark:hover:bg-red-900/30"
                              title="Delete"
                            >
                              <Trash2 className="w-3.5 h-3.5" />
                            </button>
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            </div>
          )}

          {/* TAB 2: WORKSHEET GENERATOR */}
          {activeTab === 'generator' && (
            <div className="max-w-4xl space-y-8">
              <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-8 shadow-sm space-y-6">
                <h3 className="text-xl font-black text-slate-900 dark:text-white mb-2">Worksheet Specifications</h3>
                
                {/* Generation Mode Toggles */}
                <div className="flex border-b border-slate-200 dark:border-slate-800">
                  <button 
                    onClick={() => setInputMode('TOPIC')}
                    className={`pb-3 px-4 font-bold text-sm border-b-2 transition ${
                      inputMode === 'TOPIC' ? 'border-indigo-600 text-indigo-600' : 'border-transparent text-slate-400'
                    }`}
                  >
                    Select Syllabus Topic
                  </button>
                  <button 
                    onClick={() => setInputMode('HTML')}
                    className={`pb-3 px-4 font-bold text-sm border-b-2 transition ${
                      inputMode === 'HTML' ? 'border-indigo-600 text-indigo-600' : 'border-transparent text-slate-400'
                    }`}
                  >
                    Input Custom HTML Code
                  </button>
                </div>

                {inputMode === 'TOPIC' ? (
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div>
                      <label className="block text-xs font-extrabold text-slate-500 uppercase tracking-widest mb-2">Grammar Topic</label>
                      <select 
                        value={selectedTopic.id}
                        onChange={(e) => {
                          const topic = GRAMMAR_TOPICS.find(t => t.id === e.target.value);
                          if (topic) setSelectedTopic(topic);
                        }}
                        className="w-full rounded-xl border border-slate-200 dark:border-slate-800 p-3 bg-slate-50 dark:bg-slate-800 font-bold focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                      >
                        {GRAMMAR_TOPICS.map(topic => (
                          <option key={topic.id} value={topic.id}>{topic.title} ({topic.difficulty})</option>
                        ))}
                      </select>
                    </div>

                    <div>
                      <label className="block text-xs font-extrabold text-slate-500 uppercase tracking-widest mb-2">Difficulty Level</label>
                      <div className="grid grid-cols-3 gap-2">
                        {(['BEGINNER', 'INTERMEDIATE', 'ADVANCED'] as const).map(level => (
                          <button 
                            key={level}
                            type="button"
                            onClick={() => setDifficulty(level)}
                            className={`py-3 rounded-xl text-xs font-bold transition capitalize ${
                              difficulty === level 
                                ? 'bg-indigo-600 text-white shadow-sm' 
                                : 'bg-slate-50 dark:bg-slate-800 text-slate-600 dark:text-slate-300 border border-slate-200 dark:border-slate-800'
                            }`}
                          >
                            {level.toLowerCase()}
                          </button>
                        ))}
                      </div>
                    </div>
                  </div>
                ) : (
                  <div className="space-y-4">
                    <div>
                      <label className="block text-xs font-extrabold text-slate-500 uppercase tracking-widest mb-2">Paste Web/HTML Excerpt</label>
                      <textarea 
                        value={rawHtmlInput}
                        onChange={(e) => setRawHtmlInput(e.target.value)}
                        placeholder="Paste raw HTML source code or paragraph text here. The AI will parse sentences, analyze grammar patterns, and generate matching activities."
                        rows={6}
                        className="w-full rounded-2xl border border-slate-200 dark:border-slate-800 p-4 bg-slate-50 dark:bg-slate-800 focus:ring-2 focus:ring-indigo-500 focus:outline-none font-mono text-xs"
                      />
                    </div>
                  </div>
                )}

                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div>
                    <label className="block text-xs font-extrabold text-slate-500 uppercase tracking-widest mb-2">Worksheet Template Layout</label>
                    <select 
                      value={worksheetTemplate}
                      onChange={(e) => setWorksheetTemplate(e.target.value as WorksheetTemplate)}
                      className="w-full rounded-xl border border-slate-200 dark:border-slate-800 p-3 bg-slate-50 dark:bg-slate-800 font-bold focus:outline-none"
                    >
                      <option value="MIXED_FORMAT">Mixed Questions (MCQ, Fill, Short)</option>
                      <option value="FILL_IN_BLANKS">Fill in the Blanks Exclusive</option>
                      <option value="MULTIPLE_CHOICE">Multiple Choice Quiz Format</option>
                      <option value="SENTENCE_CORRECTION">Sentence Transform / Rewrite</option>
                    </select>
                  </div>

                  <div>
                    <label className="block text-xs font-extrabold text-slate-500 uppercase tracking-widest mb-2">Structural Content</label>
                    <select 
                      value={outputStructure}
                      onChange={(e) => setOutputStructure(e.target.value as OutputStructure)}
                      className="w-full rounded-xl border border-slate-200 dark:border-slate-800 p-3 bg-slate-50 dark:bg-slate-800 font-bold focus:outline-none"
                    >
                      <option value="EXPLANATION_AND_EXERCISES">Lesson Grammar Theory + Exercises</option>
                      <option value="EXERCISES_ONLY">Exercises and Questions Only</option>
                      <option value="MULTIPLE_CHOICE_QUIZ">Standard Classroom Assessment Quiz</option>
                    </select>
                  </div>
                </div>

                <div className="border-t border-slate-100 dark:border-slate-800 pt-6">
                  <div className="flex items-center justify-between">
                    <div>
                      <h4 className="font-bold text-sm text-slate-950 dark:text-white">Detailed Answer Explanations</h4>
                      <p className="text-xs text-slate-400">Include clear grammar rules under questions during results display</p>
                    </div>
                    <button 
                      onClick={() => setIncludeExplanations(!includeExplanations)}
                      className={`relative inline-flex h-6 w-11 shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none ${
                        includeExplanations ? 'bg-indigo-600' : 'bg-slate-200 dark:bg-slate-700'
                      }`}
                    >
                      <span className={`pointer-events-none inline-block h-5 w-5 transform rounded-full bg-white shadow ring-0 transition duration-200 ease-in-out ${
                        includeExplanations ? 'translate-x-5' : 'translate-x-0'
                      }`} />
                    </button>
                  </div>
                </div>

                <div className="pt-4">
                  <label className="block text-xs font-extrabold text-slate-500 uppercase tracking-widest mb-2">Specific Rule Focus (Optional)</label>
                  <input 
                    type="text" 
                    value={specificRuleFilter}
                    onChange={(e) => setSpecificRuleFilter(e.target.value)}
                    placeholder="e.g. Focus on perfect continuous conditional shifts, ignore future continuous passive."
                    className="w-full rounded-xl border border-slate-200 dark:border-slate-800 p-3 bg-slate-50 dark:bg-slate-800 focus:outline-none text-sm"
                  />
                </div>

                {isGenerating ? (
                  <div className="bg-slate-50 dark:bg-slate-800/50 border border-slate-100 dark:border-slate-800 p-6 rounded-2xl flex flex-col items-center justify-center space-y-4">
                    <div className="relative w-12 h-12">
                      <div className="absolute w-full h-full rounded-full border-4 border-indigo-200 dark:border-slate-800" />
                      <div className="absolute w-full h-full rounded-full border-4 border-t-indigo-600 animate-spin" />
                    </div>
                    <span className="text-sm font-bold text-indigo-600 dark:text-indigo-400 animate-pulse">{progressMessage}</span>
                  </div>
                ) : (
                  <button 
                    onClick={triggerGeneration}
                    className={`w-full flex items-center justify-center gap-2 py-4 rounded-xl text-white font-extrabold shadow-md hover:scale-[1.01] hover:shadow-lg transition ${currentTheme.primary}`}
                  >
                    <Sparkles className="w-5 h-5" />
                    <span>Synthesize AI Grammar Worksheet</span>
                  </button>
                )}
              </div>
            </div>
          )}

          {/* TAB 3: INTERACTIVE PRACTICE & PRINT-VIEW */}
          {activeTab === 'practice' && activeWorksheet && (
            <div className="max-w-5xl grid grid-cols-1 lg:grid-cols-3 gap-8">
              
              {/* Worksheet Paper Core Container */}
              <div className="lg:col-span-2 space-y-8 print-container">
                <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-10 shadow-sm relative overflow-hidden">
                  
                  {/* Decorative Template Header Band */}
                  <div className={`absolute left-0 top-0 right-0 h-3 ${currentTheme.primary}`} />

                  {/* Worksheet Header Layout */}
                  <div className="border-b border-slate-100 dark:border-slate-800 pb-6 mb-8 flex justify-between items-start pt-2">
                    <div>
                      <span className="text-[10px] font-black uppercase tracking-widest text-indigo-600 dark:text-indigo-400">
                        {subscription.schoolName}
                      </span>
                      <h3 className="text-2xl font-black text-slate-900 dark:text-white tracking-tight mt-1 leading-tight">
                        {activeWorksheet.title}
                      </h3>
                      <div className="flex gap-4 mt-2 text-xs text-slate-400 font-bold uppercase tracking-wider">
                        <span>Topic: {activeWorksheet.topic}</span>
                        <span>&bull;</span>
                        <span>Level: {activeWorksheet.difficulty}</span>
                      </div>
                    </div>
                    <div className="text-right text-xs text-slate-400 font-bold shrink-0">
                      <div>Instructor: {subscription.userName}</div>
                      <div>Date: {new Date().toLocaleDateString()}</div>
                    </div>
                  </div>

                  {/* Theory Lesson Intro (if selected) */}
                  {outputStructure === 'EXPLANATION_AND_EXERCISES' && (
                    <div className="mb-8 p-6 bg-slate-50 dark:bg-slate-800/40 rounded-2xl border border-slate-100 dark:border-slate-800">
                      <div className="flex items-center gap-2 mb-3 text-indigo-600 dark:text-indigo-400">
                        <Info className="w-5 h-5 shrink-0" />
                        <span className="font-extrabold text-sm uppercase tracking-wider">Grammatical Foundation Lesson</span>
                      </div>
                      <p className="text-sm text-slate-600 dark:text-slate-300 leading-relaxed font-medium">
                        Focus Area: <strong className="text-slate-900 dark:text-white">{activeWorksheet.targetRules}</strong>.
                        To master this, ensure proper coordination of structural components, auxiliary alignment, shifting rules, and target tense matching. Review hints on each question if you require assistance.
                      </p>
                    </div>
                  )}

                  {/* Exercises */}
                  <div className="space-y-8">
                    {activeWorksheet.questions.map((q, idx) => (
                      <div key={q.id} className="space-y-3">
                        <div className="flex gap-2">
                          <span className="font-black text-indigo-600 dark:text-indigo-400 text-sm">{idx + 1}.</span>
                          <span className="font-extrabold text-slate-900 dark:text-white text-base">
                            {q.questionText}
                          </span>
                        </div>

                        {/* Rendering different types of questions */}
                        {q.type === 'MULTIPLE_CHOICE' ? (
                          <div className="grid grid-cols-1 md:grid-cols-2 gap-3 pl-6">
                            {q.options.map(opt => {
                              const isSelected = userAnswers[q.id] === opt;
                              const isCorrect = q.correctAnswer === opt;
                              const showResultsStyle = showResults 
                                ? isCorrect 
                                  ? 'bg-emerald-50 dark:bg-emerald-950/20 border-emerald-500 text-emerald-700 dark:text-emerald-300' 
                                  : isSelected 
                                    ? 'bg-red-50 dark:bg-red-950/20 border-red-500 text-red-700 dark:text-red-300' 
                                    : 'opacity-50'
                                : isSelected 
                                  ? 'bg-indigo-50 dark:bg-indigo-950/20 border-indigo-500 text-indigo-900 dark:text-indigo-200'
                                  : 'hover:bg-slate-50 dark:hover:bg-slate-800/50 border-slate-200 dark:border-slate-800';

                              return (
                                <button
                                  key={opt}
                                  onClick={() => {
                                    if (showResults) return;
                                    setUserAnswers({ ...userAnswers, [q.id]: opt });
                                  }}
                                  className={`w-full text-left p-3.5 rounded-xl border text-sm font-bold transition ${showResultsStyle}`}
                                >
                                  {opt}
                                </button>
                              );
                            })}
                          </div>
                        ) : (
                          <div className="pl-6 space-y-2">
                            <input 
                              type="text"
                              value={userAnswers[q.id] || ''}
                              onChange={(e) => {
                                if (showResults) return;
                                setUserAnswers({ ...userAnswers, [q.id]: e.target.value });
                              }}
                              disabled={showResults}
                              placeholder="Type your answer here..."
                              className={`w-full max-w-lg rounded-xl border p-3 font-bold text-sm focus:outline-none transition ${
                                showResults 
                                  ? (userAnswers[q.id]?.trim().toLowerCase() === q.correctAnswer.trim().toLowerCase())
                                    ? 'bg-emerald-50 dark:bg-emerald-950/20 border-emerald-500 text-emerald-700 dark:text-emerald-300'
                                    : 'bg-red-50 dark:bg-red-950/20 border-red-500 text-red-700 dark:text-red-300'
                                  : 'bg-slate-50 dark:bg-slate-800 border-slate-200 dark:border-slate-800 focus:ring-2 focus:ring-indigo-500'
                              }`}
                            />
                            {showResults && userAnswers[q.id]?.trim().toLowerCase() !== q.correctAnswer.trim().toLowerCase() && (
                              <p className="text-xs text-emerald-600 dark:text-emerald-400 font-extrabold pt-1">
                                Expected: "{q.correctAnswer}"
                              </p>
                            )}
                          </div>
                        )}

                        {/* Hint / Explanation Blocks */}
                        <div className="pl-6 flex flex-col gap-1.5 pt-1">
                          <div className="flex items-center gap-1.5 text-xs text-slate-400 font-bold">
                            <HelpCircle className="w-3.5 h-3.5" />
                            <span>Hint: {q.hint}</span>
                          </div>
                          {showResults && includeExplanations && (
                            <div className="p-3.5 bg-indigo-50/50 dark:bg-indigo-950/20 border-l-4 border-indigo-500 rounded-r-xl mt-2">
                              <p className="text-xs text-slate-600 dark:text-slate-300 leading-relaxed font-semibold">
                                <strong>Rationale:</strong> {q.explanation}
                              </p>
                            </div>
                          )}
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              </div>

              {/* Sidebar controls for printing & saving */}
              <div className="space-y-6 no-print">
                <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-sm space-y-5">
                  <h4 className="font-extrabold text-base text-slate-990 dark:text-white">Workspace Actions</h4>
                  
                  <div className="flex items-center justify-between pb-3 border-b border-slate-100 dark:border-slate-800">
                    <span className="text-xs font-bold text-slate-400 uppercase">Save to Library</span>
                    <button 
                      onClick={handleSaveToLibrary}
                      className="inline-flex items-center gap-1 text-xs font-black text-indigo-600 dark:text-indigo-400 hover:underline"
                    >
                      {activeWorksheet.isSaved ? (
                        <>
                          <BookmarkCheck className="w-4 h-4 text-emerald-500" />
                          <span>Bookmarked</span>
                        </>
                      ) : (
                        <>
                          <Bookmark className="w-4 h-4" />
                          <span>Add to Library</span>
                        </>
                      )}
                    </button>
                  </div>

                  <div className="space-y-2">
                    <button 
                      onClick={handlePrintWorksheet}
                      className="w-full flex items-center justify-center gap-2 py-3 bg-indigo-600 hover:bg-indigo-700 text-white font-extrabold rounded-xl shadow-sm transition"
                    >
                      <Printer className="w-4 h-4" />
                      <span>Print / Export PDF A4</span>
                    </button>

                    <button 
                      onClick={() => {
                        const content = activeWorksheet.questions.map((q, idx) => `${idx + 1}. ${q.questionText}\n`).join('\n');
                        handleCopyText(content, 'copy_lesson');
                      }}
                      className="w-full flex items-center justify-center gap-2 py-3 bg-slate-50 hover:bg-slate-100 dark:bg-slate-800 dark:hover:bg-slate-700 font-bold rounded-xl text-sm transition"
                    >
                      {copiedId === 'copy_lesson' ? <Check className="w-4 h-4 text-emerald-500" /> : <Copy className="w-4 h-4" />}
                      <span>Copy Worksheet Text</span>
                    </button>
                  </div>
                </div>

                {/* Score Results Panel */}
                <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-sm space-y-4">
                  <h4 className="font-extrabold text-base text-slate-900 dark:text-white">Solve & Assess</h4>
                  {showResults ? (
                    <div className="space-y-4 text-center">
                      <div className="inline-flex items-center justify-center w-20 h-20 rounded-full bg-indigo-50 dark:bg-indigo-950/50 border-4 border-indigo-600 mb-1">
                        <span className="font-black text-xl text-indigo-600 dark:text-indigo-400">{calculateScore()}%</span>
                      </div>
                      <div>
                        <h5 className="font-extrabold text-slate-900 dark:text-white">Assessment Complete!</h5>
                        <p className="text-xs text-slate-400 mt-1">Excellent work practicing your grammar patterns.</p>
                      </div>
                      <button 
                        onClick={() => {
                          setShowResults(false);
                          setUserAnswers({});
                        }}
                        className="w-full py-2.5 bg-slate-50 hover:bg-slate-100 dark:bg-slate-800 text-xs font-extrabold rounded-xl transition"
                      >
                        Reset and Retry
                      </button>
                    </div>
                  ) : (
                    <div className="space-y-3">
                      <p className="text-xs text-slate-400 font-medium">
                        Answer all exercises above then click finish to check your correctness and display rationals.
                      </p>
                      <button 
                        onClick={handleFinishPractice}
                        className="w-full py-3 bg-emerald-600 hover:bg-emerald-700 text-white font-extrabold rounded-xl shadow-sm transition"
                      >
                        Check My Answers
                      </button>
                    </div>
                  )}
                </div>
              </div>
            </div>
          )}

          {/* TAB 4: SAVED WORKSHEETS LIBRARY */}
          {activeTab === 'saved' && (
            <div className="space-y-6 max-w-5xl">
              {savedWorksheets.length === 0 ? (
                <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-16 text-center space-y-3">
                  <FileText className="w-12 h-12 text-slate-300 mx-auto" />
                  <h4 className="font-extrabold text-slate-900 dark:text-white">No worksheets in library</h4>
                  <p className="text-sm text-slate-500 max-w-md mx-auto">
                    Generate some worksheets using the generator workspace and bookmark them to keep them here indefinitely.
                  </p>
                </div>
              ) : (
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  {savedWorksheets.map(ws => (
                    <div 
                      key={ws.id}
                      onClick={() => {
                        setActiveWorksheet(ws);
                        setUserAnswers({});
                        setShowResults(false);
                        setActiveTab('practice');
                      }}
                      className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 cursor-pointer hover:shadow-md hover:border-indigo-400 transition"
                    >
                      <div className="flex justify-between items-start mb-2">
                        <h4 className="font-extrabold text-slate-900 dark:text-white text-base">
                          {ws.title}
                        </h4>
                        <button 
                          onClick={(e) => {
                            e.stopPropagation();
                            handleToggleFavorite(ws.id!);
                          }}
                          className="p-1 rounded hover:bg-slate-100 dark:hover:bg-slate-800"
                        >
                          <Heart className={`w-4 h-4 ${ws.isFavorite ? 'fill-red-500 text-red-500' : 'text-slate-400'}`} />
                        </button>
                      </div>

                      <div className="flex gap-4 text-xs font-bold text-slate-400 mb-6">
                        <span>Topic: {ws.topic}</span>
                        <span>&bull;</span>
                        <span>Level: {ws.difficulty}</span>
                      </div>

                      <div className="flex justify-between items-center border-t border-slate-100 dark:border-slate-800 pt-4 mt-2">
                        <span className="text-xs font-black text-indigo-600 dark:text-indigo-400">Open & Solve Worksheet &rarr;</span>
                        <div className="flex gap-2">
                          <button 
                            onClick={(e) => {
                              e.stopPropagation();
                              setActiveWorksheet(ws);
                              setActiveTab('practice');
                              setTimeout(() => window.print(), 100);
                            }}
                            className="p-2.5 rounded-lg bg-slate-50 hover:bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-300"
                            title="Export PDF"
                          >
                            <Download className="w-3.5 h-3.5" />
                          </button>
                          <button 
                            onClick={(e) => handleDeleteWorksheet(ws.id!, e)}
                            className="p-2.5 rounded-lg bg-red-50 hover:bg-red-100 text-red-600 dark:bg-red-950/20"
                            title="Delete"
                          >
                            <Trash2 className="w-3.5 h-3.5" />
                          </button>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}

          {/* TAB 5: ANALYTICS & INSIGHTS */}
          {activeTab === 'analytics' && (
            <div className="space-y-8 max-w-5xl">
              <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
                <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-sm">
                  <span className="text-xs font-bold text-slate-400 uppercase">Lessons Generated</span>
                  <h3 className="text-3xl font-black text-slate-900 dark:text-white mt-1">24</h3>
                  <p className="text-[11px] text-emerald-500 font-bold mt-2">&uarr; 12% increase this week</p>
                </div>
                <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-sm">
                  <span className="text-xs font-bold text-slate-400 uppercase">Library Size</span>
                  <h3 className="text-3xl font-black text-slate-900 dark:text-white mt-1">{savedWorksheets.length}</h3>
                  <p className="text-[11px] text-slate-400 font-bold mt-2">Active practice assets</p>
                </div>
                <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-sm">
                  <span className="text-xs font-bold text-slate-400 uppercase">Avg Mastery Score</span>
                  <h3 className="text-3xl font-black text-slate-900 dark:text-white mt-1">88%</h3>
                  <p className="text-[11px] text-emerald-500 font-bold mt-2">&uarr; Past 10 quizzes reviewed</p>
                </div>
              </div>

              {/* Graphical representation mock */}
              <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-8 shadow-sm space-y-6">
                <h4 className="font-extrabold text-slate-900 dark:text-white">Topic Mastery Breakdown</h4>
                <div className="space-y-4">
                  <div>
                    <div className="flex justify-between text-xs font-bold mb-1">
                      <span>Verb Tenses Mastery</span>
                      <span className="text-indigo-600 dark:text-indigo-400">92%</span>
                    </div>
                    <div className="w-full bg-slate-100 dark:bg-slate-800 h-3 rounded-full overflow-hidden">
                      <div className="bg-indigo-600 h-full rounded-full" style={{ width: '92%' }} />
                    </div>
                  </div>

                  <div>
                    <div className="flex justify-between text-xs font-bold mb-1">
                      <span>Passive Voice Mastery</span>
                      <span className="text-indigo-600 dark:text-indigo-400">85%</span>
                    </div>
                    <div className="w-full bg-slate-100 dark:bg-slate-800 h-3 rounded-full overflow-hidden">
                      <div className="bg-indigo-600 h-full rounded-full" style={{ width: '85%' }} />
                    </div>
                  </div>

                  <div>
                    <div className="flex justify-between text-xs font-bold mb-1">
                      <span>Conditionals Mastery</span>
                      <span className="text-indigo-600 dark:text-indigo-400">74%</span>
                    </div>
                    <div className="w-full bg-slate-100 dark:bg-slate-800 h-3 rounded-full overflow-hidden">
                      <div className="bg-indigo-600 h-full rounded-full" style={{ width: '74%' }} />
                    </div>
                  </div>
                </div>
              </div>
            </div>
          )}

          {/* TAB 6: SYSTEM SETTINGS */}
          {activeTab === 'settings' && (
            <div className="max-w-4xl space-y-8">
              <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-8 shadow-sm space-y-6">
                <h3 className="text-lg font-black text-slate-900 dark:text-white">Branding Configuration</h3>
                
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div>
                    <label className="block text-xs font-extrabold text-slate-500 uppercase tracking-widest mb-2">Educator Name</label>
                    <input 
                      type="text"
                      value={subscription.userName}
                      onChange={(e) => {
                        const updated = { ...subscription, userName: e.target.value };
                        setSubscription(updated);
                        localStorage.setItem('user_subscription', JSON.stringify(updated));
                      }}
                      className="w-full rounded-xl border border-slate-200 dark:border-slate-800 p-3 bg-slate-50 dark:bg-slate-800 font-bold focus:outline-none"
                    />
                  </div>

                  <div>
                    <label className="block text-xs font-extrabold text-slate-500 uppercase tracking-widest mb-2">School / Institute Name</label>
                    <input 
                      type="text"
                      value={subscription.schoolName}
                      onChange={(e) => {
                        const updated = { ...subscription, schoolName: e.target.value };
                        setSubscription(updated);
                        localStorage.setItem('user_subscription', JSON.stringify(updated));
                      }}
                      className="w-full rounded-xl border border-slate-200 dark:border-slate-800 p-3 bg-slate-50 dark:bg-slate-800 font-bold focus:outline-none"
                    />
                  </div>
                </div>

                <div className="border-t border-slate-100 dark:border-slate-800 pt-6 space-y-4">
                  <h4 className="font-extrabold text-sm text-slate-900 dark:text-white">App Color Palette</h4>
                  <div className="grid grid-cols-2 md:grid-cols-4 gap-3">
                    {THEMES.map(theme => (
                      <button 
                        key={theme.id}
                        onClick={() => {
                          setAppTheme(theme.id);
                          localStorage.setItem('app_theme', theme.id);
                        }}
                        className={`p-3.5 rounded-xl border text-left font-bold text-xs transition ${
                          appTheme === theme.id 
                            ? 'border-indigo-600 bg-indigo-50/20 text-indigo-900 dark:text-indigo-200' 
                            : 'border-slate-200 dark:border-slate-800 text-slate-600 dark:text-slate-300'
                        }`}
                      >
                        <div className="flex items-center gap-2">
                          <span className={`w-3 h-3 rounded-full ${theme.primary}`} />
                          <span>{theme.name}</span>
                        </div>
                      </button>
                    ))}
                  </div>
                </div>

                {/* Dark Mode Settings Card */}
                <div className="border-t border-slate-100 dark:border-slate-800 pt-6">
                  <div className="flex items-center justify-between">
                    <div>
                      <h4 className="font-bold text-sm text-slate-950 dark:text-white">Dark Theme Mode</h4>
                      <p className="text-xs text-slate-400">Enables comfortable low-light reading & practice</p>
                    </div>
                    <button 
                      onClick={() => {
                        const newVal = !isDarkMode;
                        setIsDarkMode(newVal);
                        localStorage.setItem('dark_mode', String(newVal));
                      }}
                      className={`relative inline-flex h-6 w-11 shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none ${
                        isDarkMode ? 'bg-indigo-600' : 'bg-slate-200 dark:bg-slate-700'
                      }`}
                    >
                      <span className={`pointer-events-none inline-block h-5 w-5 transform rounded-full bg-white shadow ring-0 transition duration-200 ease-in-out ${
                        isDarkMode ? 'translate-x-5' : 'translate-x-0'
                      }`} />
                    </button>
                  </div>
                </div>

                {/* Dev Tier Premium Toggle */}
                <div className="border-t border-slate-100 dark:border-slate-800 pt-6">
                  <div className="flex items-center justify-between">
                    <div>
                      <h4 className="font-bold text-sm text-slate-950 dark:text-white">Developer Mode Tier</h4>
                      <p className="text-xs text-slate-400">Bypass paywalls and unlock Premium Educator benefits</p>
                    </div>
                    <button 
                      onClick={() => {
                        const updated = { ...subscription, isPremium: !subscription.isPremium };
                        setSubscription(updated);
                        localStorage.setItem('user_subscription', JSON.stringify(updated));
                      }}
                      className={`relative inline-flex h-6 w-11 shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none ${
                        subscription.isPremium ? 'bg-amber-500' : 'bg-slate-200 dark:bg-slate-700'
                      }`}
                    >
                      <span className={`pointer-events-none inline-block h-5 w-5 transform rounded-full bg-white shadow ring-0 transition duration-200 ease-in-out ${
                        subscription.isPremium ? 'translate-x-5' : 'translate-x-0'
                      }`} />
                    </button>
                  </div>
                </div>
              </div>
            </div>
          )}

        </div>
      </main>

      {/* PREMIUM PAYWALL MODAL SCREEN */}
      {showPaywall && (
        <div className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white dark:bg-slate-900 rounded-3xl max-w-md w-full p-8 shadow-2xl border border-slate-200 dark:border-slate-800 space-y-6 text-center">
            <div className="inline-flex p-3 rounded-2xl bg-amber-500/10 text-amber-500">
              <Award className="w-8 h-8 animate-bounce" />
            </div>
            
            <div className="space-y-2">
              <h3 className="text-xl font-extrabold text-slate-900 dark:text-white">Unlock Educator Premium</h3>
              <p className="text-xs text-slate-500 dark:text-slate-400 leading-relaxed">
                {paywallReason || 'Get unrestricted access to custom HTML syllabus extractions, printing capabilities, and unlimited sheets.'}
              </p>
            </div>

            <div className="bg-slate-50 dark:bg-slate-800/50 p-4 rounded-2xl text-left space-y-2">
              <div className="flex items-center gap-2 text-xs font-bold">
                <CheckCircle className="w-4 h-4 text-emerald-500" />
                <span>Unlimited generation requests</span>
              </div>
              <div className="flex items-center gap-2 text-xs font-bold">
                <CheckCircle className="w-4 h-4 text-emerald-500" />
                <span>Branded headers & watermark-free PDFs</span>
              </div>
              <div className="flex items-center gap-2 text-xs font-bold">
                <CheckCircle className="w-4 h-4 text-emerald-500" />
                <span>No daily token throttles</span>
              </div>
            </div>

            <div className="flex gap-3 pt-2">
              <button 
                onClick={() => setShowPaywall(false)}
                className="flex-1 py-3 border border-slate-200 dark:border-slate-800 hover:bg-slate-50 dark:hover:bg-slate-800 rounded-xl font-bold text-sm transition"
              >
                Keep Free Tier
              </button>
              <button 
                onClick={() => {
                  const updated = { ...subscription, isPremium: true };
                  setSubscription(updated);
                  localStorage.setItem('user_subscription', JSON.stringify(updated));
                  setShowPaywall(false);
                }}
                className={`flex-1 py-3 text-white font-extrabold rounded-xl shadow-md transition bg-amber-500 hover:bg-amber-600`}
              >
                Enable Developer Tier
              </button>
            </div>
          </div>
        </div>
      )}

    </div>
  );
}
