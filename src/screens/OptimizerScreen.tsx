import { motion } from 'motion/react';
import { Cpu, Thermometer, HardDrive, MemoryStick, Activity, Zap } from 'lucide-react';
import { useApp } from '../AppContext';

export function OptimizerScreen() {
  const { state, dispatch } = useApp();

  return (
    <div className="h-full flex flex-col pt-12 px-6 pb-24 overflow-y-auto custom-scrollbar">
      <h2 className="text-3xl font-bold text-white mb-2">Optimizer</h2>
      <p className="text-zinc-400 mb-8">Hardware analysis and performance tuning.</p>

      {/* Device Score Gauge Simulation */}
      <div className="bg-zinc-900 border border-zinc-800 rounded-3xl p-8 flex flex-col items-center justify-center relative overflow-hidden mb-6 shadow-xl">
        <div className="absolute top-0 right-0 w-32 h-32 bg-emerald-500/10 rounded-full blur-3xl -mr-10 -mt-10" />
        
        <div className="relative w-40 h-40 flex items-center justify-center">
          <svg className="w-full h-full transform -rotate-90">
            <circle cx="80" cy="80" r="70" fill="none" stroke="#27272a" strokeWidth="12" strokeLinecap="round" />
            <circle 
              cx="80" cy="80" r="70" fill="none" 
              stroke="#10b981" strokeWidth="12" strokeLinecap="round" 
              strokeDasharray="439.8" 
              strokeDashoffset={439.8 - (439.8 * state.deviceScore) / 100}
              className="transition-all duration-1000 ease-out"
            />
          </svg>
          <div className="absolute flex flex-col items-center justify-center">
            <span className="text-4xl font-black text-white">{state.deviceScore}</span>
            <span className="text-[10px] font-bold text-zinc-400 uppercase tracking-widest mt-1">Score</span>
          </div>
        </div>

        <h3 className="text-xl font-bold text-zinc-100 mt-6">Flagship Tier</h3>
        <p className="text-zinc-500 text-sm text-center mt-2 max-w-xs">
          Your device is highly capable. Recommended baseline profile is <span className="text-emerald-400 font-bold">Ultra</span>.
        </p>

        <button 
          onClick={() => dispatch({ type: 'RUN_BENCHMARK' })}
          className="mt-6 px-6 py-2.5 bg-zinc-800 hover:bg-zinc-700 text-white rounded-full font-bold text-sm transition-colors flex items-center gap-2"
        >
          <Activity className="w-4 h-4" />
          Run Benchmark
        </button>
      </div>

      <h3 className="text-lg font-bold text-zinc-100 mb-4">Hardware Status</h3>
      
      <div className="grid grid-cols-2 gap-3 mb-6">
        <StatusCard icon={Cpu} title="CPU" value="Snapdragon 8 Gen 2" stat="Max 3.2GHz" color="text-blue-400" bg="bg-blue-500/10" />
        <StatusCard icon={Zap} title="GPU" value="Adreno 740" stat="Vulkan 1.3" color="text-purple-400" bg="bg-purple-500/10" />
        <StatusCard icon={MemoryStick} title="RAM" value="12 GB Total" stat="4.2 GB Free" color="text-amber-400" bg="bg-amber-500/10" />
        <StatusCard icon={Thermometer} title="Thermal" value="Normal" stat="34°C" color="text-emerald-400" bg="bg-emerald-500/10" />
      </div>

      <div className="bg-zinc-900 border border-zinc-800 rounded-3xl p-5 flex items-center gap-4">
        <div className="w-12 h-12 rounded-2xl bg-zinc-800 flex items-center justify-center flex-shrink-0">
          <HardDrive className="w-6 h-6 text-zinc-400" />
        </div>
        <div className="flex-1">
          <h4 className="font-bold text-zinc-200">Storage Speed</h4>
          <p className="text-sm text-zinc-500">UFS 4.0 Detected • 3500 MB/s</p>
        </div>
      </div>
    </div>
  );
}

function StatusCard({ icon: Icon, title, value, stat, color, bg }: any) {
  return (
    <div className="bg-zinc-900 border border-zinc-800 rounded-2xl p-4 flex flex-col gap-3">
      <div className={`w-10 h-10 rounded-xl ${bg} flex items-center justify-center`}>
        <Icon className={`w-5 h-5 ${color}`} />
      </div>
      <div>
        <h4 className="font-bold text-zinc-200 text-sm">{title}</h4>
        <p className="text-zinc-400 text-xs mt-0.5">{value}</p>
        <p className="text-zinc-500 text-[10px] font-mono mt-1">{stat}</p>
      </div>
    </div>
  );
}
