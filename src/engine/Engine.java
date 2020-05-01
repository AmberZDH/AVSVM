package engine;

import compiler.ConvertSrcToBytecodes;
import loader.LoadBytecodes;

import java.io.File;

public class Engine {
    private LoadBytecodes loadBytecodes;

    public Engine() {
        String codefilename = "";
        try {
            System.out.println("编译启动");
            codefilename = srcTocode();
            System.out.println("编译完成");
        } catch (Exception e) {
            System.out.println("ERROR编译失败!!");
        }

        try {
            System.out.println("开始装载");
            loadBytecodes = new LoadBytecodes(codefilename);
        } catch (Exception e) {
            System.out.println("ERROR装载失败!!");
        }

        try {
            System.out.println("指令执行");
            String command = "";
            while (!command.equals("HALT")) {
                command = this.loadBytecodes.getPCommand();
                parseCommand(command);
                System.out.println(this.loadBytecodes.sp + "---" + command + this.loadBytecodes.stack.toString());
            }
            String result = this.loadBytecodes.stack.toString();
            System.out.println("执行结束\n" + result);
        } catch (Exception e) {
            System.out.println("ERROR指令执行失败!!");
        }
    }


    /**
     * 启动编译器
     *
     * @return
     */
    public String srcTocode() {
        File file = new File("./");
        File[] files_list = file.listFiles();
        String srcfilename = "";

        //寻找字节码文件
        for (int i = 0; i < files_list.length; i++) {
            if (files_list[i].isFile()) {
                if (files_list[i].getName().contains(".txt")) {
                    srcfilename = files_list[i].getPath();
                }
            }
        }
        ConvertSrcToBytecodes convertSrcToBytecodes = new ConvertSrcToBytecodes(srcfilename);
        convertSrcToBytecodes.convert();
        return srcfilename.replace(".txt", ".code");
    }

    /**
     * 执行指令
     *
     * @param line
     */
    public void parseCommand(String line) {
        int length = line.length();
        String command = "";
        String value = "";

        for (int i = 0; i < length; i++) {
            if (line.charAt(i) == '0' || line.charAt(i) == '1'
                    || line.charAt(i) == '2' || line.charAt(i) == '3'
                    || line.charAt(i) == '4' || line.charAt(i) == '5'
                    || line.charAt(i) == '6' || line.charAt(i) == '7'
                    || line.charAt(i) == '8' || line.charAt(i) == '9') {
                value += line.charAt(i);
            } else {
                command += line.charAt(i);
            }
        }

        String top_value = "";
        String next_top_value = "";
        switch (command) {
            case "PUSH":
                this.loadBytecodes.stack.push(value);
                this.loadBytecodes.sp += 1;
                this.loadBytecodes.pc += 1;
                break;

            case "POP":
                top_value = this.loadBytecodes.stack.pop();
                this.loadBytecodes.sp -= 1;
                this.loadBytecodes.pc += 1;
                break;

            case "DUP":
                top_value = this.loadBytecodes.stack.get(this.loadBytecodes.sp);
                this.loadBytecodes.stack.push(top_value);
                this.loadBytecodes.sp += 1;
                this.loadBytecodes.pc += 1;
                break;

            case "SWAP":
                top_value = this.loadBytecodes.stack.pop();
                next_top_value = this.loadBytecodes.stack.pop();
                this.loadBytecodes.stack.push(top_value);
                this.loadBytecodes.stack.push(next_top_value);
                this.loadBytecodes.pc += 1;
                break;

            case "ADD":
                top_value = this.loadBytecodes.stack.pop();
                next_top_value = this.loadBytecodes.stack.pop();
                int sum = Integer.parseInt(top_value) + Integer.parseInt(next_top_value);
                this.loadBytecodes.stack.push(String.valueOf(sum));
                this.loadBytecodes.sp -= 1;
                this.loadBytecodes.pc += 1;
                break;

            case "SUB":
                top_value = this.loadBytecodes.stack.pop();
                next_top_value = this.loadBytecodes.stack.pop();
                int sub = Integer.parseInt(next_top_value) - Integer.parseInt(top_value);
                this.loadBytecodes.stack.push(String.valueOf(sub));
                this.loadBytecodes.sp -= 1;
                this.loadBytecodes.pc += 1;
                break;

            case "IFEQ":
                top_value = this.loadBytecodes.stack.pop();
                this.loadBytecodes.sp -= 1;
                if (Integer.parseInt(top_value) == 0) {
                    this.loadBytecodes.pc += Integer.parseInt(value);
                    this.loadBytecodes.pc += 1;
                } else {
                    this.loadBytecodes.pc += 1;
                }
                break;

            case "IFNE":
                top_value = this.loadBytecodes.stack.pop();
                this.loadBytecodes.sp -= 1;
                if (Integer.parseInt(top_value) != 0) {
                    this.loadBytecodes.pc += Integer.parseInt(value);
                    this.loadBytecodes.pc += 1;
                } else {
                    this.loadBytecodes.pc += 1;
                }
                break;

            case "NOP":
                this.loadBytecodes.pc += 1;
                break;


        }
    }

}
