package com.inovationware.toolkit.common.utility;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ProgramRunner {
    private interface Command{
        void execute(Context context);
    }

    public static class Program implements Command {
        private String initialProgramPath = "";
        private int initialProgramInterval = 20;
        private List<String> programs;
        private int interval = 5;
        private ScheduledExecutorService initialService;
        private ScheduledExecutorService service;

        private Program(String initialProgramPath, List<String> programs, int initialProgramInterval, int interval){
            this.initialProgramPath = initialProgramPath;
            this.initialProgramInterval = initialProgramInterval;
            this.programs = programs;
            this.interval = interval;
        }

        private Program(String initialProgramPath, List<String> programs){
            this.initialProgramPath = initialProgramPath;
            this.programs = programs;
        }

        private Program(List<String> programs, int interval){
            this.programs = programs;
            this.interval = interval;
        }

        private Program(List<String> programs){
            this.programs = programs;
        }

        public String getInitialProgramPath(){
            return initialProgramPath;
        }

        public int getInitialProgramInterval() {
            return initialProgramInterval;
        }

        public List<String> getPrograms() {
            return programs;
        }

        public int getInterval() {
            return interval;
        }

        public static Program create(String initialProgramPath, List<String> programs, int initialProgramInterval, int interval){
            return new Program(initialProgramPath, programs, initialProgramInterval, interval);
        }

        public static Program create(String initialProgramPath, List<String> programs){
            return new Program(initialProgramPath, programs);
        }

        public static Program create(List<String> programs, int interval){
            return new Program(programs, interval);
        }

        public static Program create(List<String> programs){
            return new Program(programs);
        }

        @Override
        public int hashCode() {
            return Objects.hash(
                    initialProgramPath, initialProgramInterval, interval, programs
            );
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (!(obj instanceof Program)) return false;
            if (Objects.equals(this, obj)) return true;
            return this.initialProgramPath.equals(((Program) obj).getInitialProgramPath()) &&
                    this.initialProgramInterval == ((Program) obj).initialProgramInterval &&
                    this.interval == ((Program) obj).interval &&
                    Objects.equals(this.programs, ((Program) obj).programs);
        }

        @Override
        public void execute(Context context){
            if (!initialProgramPath.isEmpty()){
                runInitialProgram(context);
            }else{
                runPrograms(context);
            }

        }

        private void runInitialProgram(Context context) {
            if (!initialProgramPath.isEmpty()){
                try {
                    startResource(context, initialProgramPath);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                if (!programs.isEmpty()){
                    initialService = Executors.newSingleThreadScheduledExecutor();
                    initialService.schedule(new Runnable() {
                        @Override
                        public void run() {
                            runPrograms(context);
                        }
                    }, initialProgramInterval, TimeUnit.SECONDS);
                }else{
                    shutdown(initialService);
                }
            }

        }

        private void runPrograms(Context context) {

            if (programs.isEmpty()) return;

            service = Executors.newSingleThreadScheduledExecutor();
            service.scheduleWithFixedDelay(new Runnable() {
                int programRunIndex = 0;
                @Override
                public void run() {
                    if (programRunIndex < programs.size()){
                        try {
                            startResource(context, programs.get(programRunIndex));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        programRunIndex++;
                    }else{
                        shutdown(service);
                    }
                }
            }, 0, interval, TimeUnit.SECONDS);

            shutdown(initialService);
        }

        private void shutdown(ScheduledExecutorService service) {
            if (service != null) {
                // Initiate an orderly shutdown
                service.shutdown();
                try {
                    // Wait for existing tasks to terminate
                    if (!service.awaitTermination(60, TimeUnit.SECONDS)) {
                        // Force shutdown if tasks did not terminate in the specified time
                        service.shutdownNow();
                        // Wait again for tasks to respond to being cancelled
                        if (!service.awaitTermination(60, TimeUnit.SECONDS)) {
                            //System.err.println("ScheduledExecutorService did not terminate");
                        }
                    }
                } catch (InterruptedException ex) {
                    // Re-cancel if current thread also interrupted
                    service.shutdownNow();
                    // Preserve interrupt status
                    Thread.currentThread().interrupt();
                }
            }
        }

        public static void startResource(Context context, String uri) {
            if (context.getPackageManager().getLaunchIntentForPackage(uri) != null) {
                context.startActivity(context.getPackageManager().getLaunchIntentForPackage(uri));
            }
            else{
                try{
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
                }
                catch (Exception ignored){
                }
            }
        }

    }

    private final Command program;

    private ProgramRunner(Command program){
        this.program = program;
    }

    public static ProgramRunner create(Program program){
        return new ProgramRunner(program);
    }

    public void run(Context context){
        program.execute(context);
    }
}
