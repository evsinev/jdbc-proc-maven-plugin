package com.payneteasy.jdbcproc.plugin.maven;

import com.googlecode.jdbcproc.daofactory.annotation.AStoredProcedure;
import com.googlecode.jdbcproc.daofactory.impl.procedureinfo.StoredProcedureInfo;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import static java.lang.String.format;

@Mojo(name = "generate",
        defaultPhase = LifecyclePhase.COMPILE,
        requiresDependencyResolution = ResolutionScope.COMPILE,
        requiresDependencyCollection = ResolutionScope.COMPILE)

public class ProcedureGeneratorMojo extends AbstractMojo {

    private final ProcedureInfoCreator procedureInfoCreator = new ProcedureInfoCreator();
    private final File                 dir                  = new File("target/procedures");

    /**
     * The Maven project.
     */
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        dir.mkdirs();

        ClassLoader classLoader = this.getClassLoader();
        Reflections reflections = new Reflections("", new SubTypesScanner(false), classLoader);
        reflections.getAllTypes().stream()
                .map(name -> loadClass(classLoader, name))
                .filter(this::hasProcedureAnnotation)
                .forEach(this::generateProcedure);

    }

    private void generateProcedure(Class<?> clazz) {
        getLog().info("Generating procedure for class " + clazz);


        List<StoredProcedureInfo> procedures = procedureInfoCreator.createProcedures(clazz);
        for (StoredProcedureInfo procedure : procedures) {
            File file = new File(dir, procedure.getProcedureName() + ".prc");
            getLog().info(format("    Writing %s() to %s ...", procedure.getProcedureName(), file.getAbsolutePath()));
            try {
                ProcedureWriter writer = new ProcedureWriter(file);
                writer.write(procedure);
            } catch (IOException e) {
                throw new IllegalStateException("Cannot write " + file.getAbsolutePath(), e);
            }
        }


    }

    private boolean hasProcedureAnnotation(Class<?> clazz) {
        for (Method method : ClassUtils.getAllMethods(clazz)) {
            if(method.isAnnotationPresent(AStoredProcedure.class)) {
                return true;
            }
        }
        return false;
    }

    private Class<?> loadClass(ClassLoader classLoader, String name) {
        try {
            return classLoader.loadClass(name);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Cannot load class " + name, e);
        }
    }

    private ClassLoader getClassLoader() {
        List<String> runtimeClasspathElements = null;
        try {
            runtimeClasspathElements = project.getRuntimeClasspathElements();
        } catch (DependencyResolutionRequiredException e) {
            throw new IllegalStateException("Failed to resolve runtime classpath elements");
            //this.getLog().error("Failed to resolve runtime classpath elements", e);
        }

        URL[] runtimeUrls = new URL[runtimeClasspathElements.size()];
        for (int i = 0; i < runtimeClasspathElements.size(); i++) {
            String element = runtimeClasspathElements.get(i);
            try {
                runtimeUrls[i] = new File(element).toURI().toURL();
            } catch (MalformedURLException e) {
                this.getLog().error("Failed to resolve runtime classpath element", e);
            }
        }
        return new URLClassLoader(runtimeUrls,
                Thread.currentThread().getContextClassLoader());

    }

}
