package com.example.opengles.procedure;

import com.example.opengles.procedure.linkprogramhandler.AttachShaderHandler;
import com.example.opengles.procedure.linkprogramhandler.CompileShaderHandler;
import com.example.opengles.procedure.linkprogramhandler.CreateProgramHandler;
import com.example.opengles.procedure.linkprogramhandler.CreateShaderHandler;
import com.example.opengles.procedure.linkprogramhandler.LinkProgramHandler;
import com.example.opengles.procedure.linkprogramhandler.ShaderSourceHandler;
import com.example.opengles.procedure.useprogramhandler.UseProgramHandler;

/**
 * 首先，需要获取链接后的着色器对象一般需要六个步骤
 *
 * - 1、创建一个顶点着色器对象和片段着色器对象
 * - 2、将源代码连接到着色器对象
 * - 3、编译着色器对象
 * - 4、创建一个程序对象
 * - 5、将编译后的着色器对象连接到程序对象
 * - 6、链接程序对象
 *
 * 如果没有错误，就可以在任何时候通知GL使用这个程序绘制
 *
 *
 */
public class TriangleChain {
    private GLHandler h1,h2,h3,h4,h5,h6;

    public TriangleChain(){
        h1 = new CreateShaderHandler();
        h2 = new ShaderSourceHandler();
        h3 = new CompileShaderHandler();
        h4 = new CreateProgramHandler();
        h5 = new AttachShaderHandler();
        h6 = new LinkProgramHandler();

        h1.setNextHandler(h2);
        h2.setNextHandler(h3);
        h3.setNextHandler(h4);
        h4.setNextHandler(h5);
        h5.setNextHandler(h6);
    }


    public void linkProgram(ProgramBean bean){
        h1.handler(bean);
    }

    public void drawTriangle(ProgramBean bean){
        new UseProgramHandler().handler(bean);
    }
}
