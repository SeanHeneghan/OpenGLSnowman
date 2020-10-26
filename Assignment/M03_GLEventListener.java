/*
	I declare that this is my own work.
	Author: Sean Heneghan
	Email: sheneghan1@sheffield.ac.uk

	This code uses Steve Maddock's java files from chapter 7's
	tutorial as a base. I then altered the code for the event listeners, 
	classes, and main to make this piece of work. The shaders were also adapted
	from Steve Maddock's tutorial code and the spotlight code came from 
	researching Joey De Vries tutorial.

*/

import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
  
public class M03_GLEventListener implements GLEventListener {
  
  private static final boolean DISPLAY_SHADERS = false;
    
  public M03_GLEventListener(Camera camera) {
    this.camera = camera;
    this.camera.setPosition(new Vec3(4f,12f,18f));
  }
  
  // ***************************************************
  /*
   * METHODS DEFINED BY GLEventListener
   */

  /* Initialisation */
  public void init(GLAutoDrawable drawable) {   
    GL3 gl = drawable.getGL().getGL3();
    System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
    gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); 
    gl.glClearDepth(1.0f);
    gl.glEnable(GL.GL_DEPTH_TEST);
    gl.glDepthFunc(GL.GL_LESS);
    gl.glFrontFace(GL.GL_CCW);    // default is 'CCW'
    gl.glEnable(GL.GL_CULL_FACE); // default is 'not enabled'
    gl.glCullFace(GL.GL_BACK);   // default is 'back', assuming CCW
    initialise(gl);
    startTime = getSeconds();
  }
  
  /* Called to indicate the drawing surface has been moved and/or resized  */
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    GL3 gl = drawable.getGL().getGL3();
    gl.glViewport(x, y, width, height);
    float aspect = (float)width/(float)height;
    camera.setPerspectiveMatrix(Mat4Transform.perspective(45, aspect));
  } 

  /* Draw */
  public void display(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    render(gl);
  }

  /* Clean up memory, if necessary */
  public void dispose(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    light.dispose(gl);
    floor.dispose(gl);
    backwall.dispose(gl);
    sphere.dispose(gl);
  }

  public double elapsedTime() {
    double elapsedTime = getSeconds()-startTime;
    return elapsedTime;
  }

    
  // ***************************************************
  /* INTERACTION
   * Each animation contains both forms of movement through the x and z axes.
   * You can enable one, the other or both, however i've commented out some as 
   * when they're all implemented together it looks very hectic. All animations are 
   * fully functional though.
   * 
   * All switches apart from the reset button are incremental, press more times in 
   * succession for greater results.
   */
   

   public void rock() {
     //uncomment below for rock on different axis.
     //rocktoAngle += 5;
     //if (rocktoAngle>=30) rocktoAngle = 30;
     rockfroAngle += 5;
     if (rockfroAngle>=30) rockfroAngle = 30;
     updateRock();
   }

   private void updateRock() {
     //uncomment for rock on different axis.
     //rockto.setTransform(Mat4Transform.rotateAroundX(rocktoAngle*(float)Math.sin(elapsedTime())));
     //rockto.update();
     rockfro.setTransform(Mat4Transform.rotateAroundZ(rockfroAngle*(float)Math.sin(elapsedTime())));
     rockfro.update();
   }
   
   public void roll() {
     //uncomment for roll on different axis.
     //rolltoAngle += 5;
     //if (rolltoAngle>=30) rolltoAngle = 30;
     rollfroAngle += 5;
     if (rollfroAngle>=30) rollfroAngle = 30;
     updateRoll();
   }
   
   private void updateRoll() {
     //uncomment for roll on different axis.
     //rollto.setTransform(Mat4Transform.rotateAroundX(rolltoAngle*(float)Math.sin(elapsedTime())));
     //rollto.update();
     rollfro.setTransform(Mat4Transform.rotateAroundZ(rollfroAngle*(float)Math.sin(elapsedTime())));
     rollfro.update();
   }

   public void slide() {
     //uncomment for slide on different axis.
     //slideDistanceIncrement += 1.0f;
     //if (slideDistanceIncrement >= 3) slideDistanceIncrement = 3;
     lateralDistanceIncrement += 1.0f;
     if (lateralDistanceIncrement >= 3) lateralDistanceIncrement = 3;
     updateSlide();
   }

   private void updateSlide() {
     //uncomment for slide on different axis.
     //slideTo.setTransform(Mat4Transform.translate(0,0,slideDistanceIncrement*(float)Math.sin(elapsedTime())));
     //slideTo.update();
     slideFro.setTransform(Mat4Transform.translate(lateralDistanceIncrement*(float)Math.sin(elapsedTime()),0,0));
     slideFro.update();
   }

   public void slideRockNRoll() {
     //all animations acting in synchronisation
     slide();
     rock();
     roll();
   }

   public void reset() {
     //reset animation stats to make snowman static
     slideDistanceIncrement = 0;
     rolltoAngle = 0;
     rocktoAngle = 0;
     lateralDistanceIncrement = 0;
     rollfroAngle = 0;
     rockfroAngle = 0;
     updateSlide();
     updateRoll();
     updateRock();
	 world_light_ambient = 0.1f;
	 world_light_diffuse = 0.4f;
	 world_light_spec = 0.8f;
	 cutOffValue = 12.5f;
   }

   public void world_light_switch() {
   	 update_world_light();
   }

   public void update_world_light() {
	 //set world light values to 0 to switch off
	 //world_light_ambient = 0;
	 //world_light_diffuse = 0;
	 //world_light_spec = 0;
	 //code above is a binary switch
	 //code below is for dimmer
	 world_light_ambient -= 0.1;
	 if (world_light_ambient <= 0) world_light_ambient = 0;
	 world_light_diffuse -= 0.1;
	 if (world_light_diffuse <= 0) world_light_diffuse = 0;
	 world_light_spec -= 0.1;
	 if (world_light_spec <= 0) world_light_spec = 0;
   }

   public void world_light_switch_up() {
   	 world_light_brighten();
   }

   public void world_light_brighten() {
     world_light_ambient += 0.1;
	 if (world_light_ambient >= 0.1) world_light_ambient = 0.1f;
	 world_light_diffuse += 0.1;
	 if (world_light_diffuse >= 0.4) world_light_diffuse = 0.4f;
	 world_light_spec += 0.1;
	 if (world_light_spec >= 0.8) world_light_spec = 0.8f;
   }

   public void spotlight_on() {
	 cutOffValue = 12.5f;
   }

   public void spotlight_off() {
     cutOffValue = 0;
   }

  // ***************************************************
  /* THE SCENE
   * Now define all the methods to handle the scene.
   * This will be added to in later examples.
   */
   
  private Camera camera;
  private Mat4 perspective, modelMatrix;
  private Model floor, backwall, sphere, features, hatblue, flannel, hatEyes, pupils, gold, gold1, post, postTop, baubleMap;
  private Light light, world_light;
  private SGNode twoBranchRoot;
  private Shader shader, shader1, backwall_shader;
  private Material material, material1, material2;
  private Mesh mesh, mesh1;

  private int[] textureId0 = new int[1];
  private int[] textureId1 = new int[1];
  private int[] textureId2 = new int[1];
  private int[] textureId3 = new int[1];
  private int[] textureId4 = new int[1];
  private int[] textureId5 = new int[1];
  private int[] textureId6 = new int[1];
  private int[] textureId7 = new int [1];
  private int[] textureId8 = new int [1];
  private int[] textureId9 = new int [1];
  private int[] textureId10 = new int [1];
  private int[] textureId11 = new int [1];
  private int[] textureId12 = new int [1];
  
  private TransformNode translateX, rotateLeftHat, rotateRightHat, reversetranslateToTop, translateToTop2;
  private TransformNode rockto, rockfro, rollto, rollfro, slideTo, slideFro, spotlightSpin, reverseSpotlightTranslate, spotlightTranslateBack;
  private float xPosition = 0;
  private float rotateAllAngleStart = 25, rotateAllAngle = rotateAllAngleStart;
  private float rotateUpperAngleStart = -60, rotateUpperAngle = rotateUpperAngleStart;
  private float rotateLeftHatAngleStart = 13, rotateLeftHatAngle = rotateLeftHatAngleStart;
  private float rotateRightHatAngleStart = -13, rotateRightHatAngle = rotateRightHatAngleStart;

  //movements
  private float rocktoAngleStart = 0, rocktoAngle = rocktoAngleStart;
  private float rockfroAngleStart = 0, rockfroAngle = rockfroAngleStart;
  private float rolltoAngleStart = 0, rolltoAngle = rolltoAngleStart;
  private float rollfroAngleStart = 0, rollfroAngle = rollfroAngleStart;
  private float slideDistanceIncrementStart = 0, slideDistanceIncrement = slideDistanceIncrementStart;
  private float lateralDistanceIncrementStart = 0, lateralDistanceIncrement = lateralDistanceIncrementStart;
  private float spotlightAngleStart = 3600000, spotlightAngle = spotlightAngleStart;

  //lighting
  private float world_light_ambient_start = 0.1f, world_light_ambient = world_light_ambient_start;
  private float world_light_diffuse_start = 0.4f, world_light_diffuse = world_light_diffuse_start;
  private float world_light_spec_start = 0.8f, world_light_spec = world_light_spec_start;
  private float light_ambient_start = 0.5f, light_ambient = light_ambient_start;
  private float light_diffuse_start = 0.8f, light_diffuse = light_diffuse_start;
  private float light_spec_start = 1.0f, light_spec = light_spec_start;
  private float cutOffValue_start = 12.5f, cutOffValue = cutOffValue_start;

  
  private void initialise(GL3 gl) {
    textureId0 = TextureLibrary.loadTexture(gl, "textures/snow.jpg");
    textureId1 = TextureLibrary.loadTexture(gl, "textures/dirty_snow.jpg");
    textureId2 = TextureLibrary.loadTexture(gl, "textures/stone.jpg");
    textureId3 = TextureLibrary.loadTexture(gl, "textures/background.jpg");
    textureId4 = TextureLibrary.loadTexture(gl, "textures/snowing.jpg");
    textureId5 = TextureLibrary.loadTexture(gl, "textures/wool.jpg");
    textureId6 = TextureLibrary.loadTexture(gl, "textures/flannel.jpg");
    textureId7 = TextureLibrary.loadTexture(gl,"textures/eyeWool.jpg");
    textureId8 = TextureLibrary.loadTexture(gl,"textures/pupilWool.jpg");
    textureId9 = TextureLibrary.loadTexture(gl,"textures/gold.jpg");
    textureId10 = TextureLibrary.loadTexture(gl, "textures/metal.jpg");
    textureId11 = TextureLibrary.loadTexture(gl, "textures/bauble.jpg");
	textureId12 = TextureLibrary.loadTexture(gl, "textures/stars.jpg");

    // initialise lights
    light = new Light(gl);
    light.setCamera(camera);
	world_light = new Light(gl);
	world_light.setCamera(camera);
    
    //initialise wall and floor
    mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    shader = new Shader(gl, "vs_tt_05.txt", "fs_tt_05.txt");
    backwall_shader = new Shader(gl, "vs_tt_06.txt", "fs_tt_06.txt");
    material = new Material(new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.8f, 0.8f, 0.8f), new Vec3(0.8f, 0.8f, 0.8f), 32.0f);
    modelMatrix = Mat4Transform.scale(16,1f,16);
    floor = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId0, textureId0);
    backwall = new Model(gl, camera, light, backwall_shader, material, modelMatrix, mesh, textureId3, textureId4);
    
    //initialise sphere models for the scene mapped with different textures
    mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    material1 = new Material(new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.8f, 0.8f, 0.8f), new Vec3(0.8f, 0.8f, 0.8f), 32.0f);
	material2 = new Material(new Vec3(1.0f, 1.0f, 1.0f), new Vec3(1.0f, 1.0f, 1.0f), new Vec3(1.0f, 1.0f, 1.0f), 64.0f);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
    sphere = new Model(gl, camera, light, shader, material1, modelMatrix, mesh, textureId1);
    features = new Model(gl, camera, light, shader, material1, modelMatrix, mesh, textureId2);
    hatblue = new Model(gl, camera, light, shader, material1, modelMatrix, mesh, textureId5);
    flannel = new Model(gl, camera, light, shader, material1, modelMatrix, mesh, textureId6);
    hatEyes = new Model(gl, camera, light, shader, material1, modelMatrix, mesh, textureId7);
    pupils = new Model(gl, camera, light, shader, material1, modelMatrix, mesh, textureId8);
    gold1 = new Model(gl, camera, light, shader, material1, modelMatrix, mesh, textureId9);
    postTop = new Model(gl, camera, light, shader, material1, modelMatrix, mesh, textureId10);
    baubleMap = new Model(gl, camera, light, shader, material2, modelMatrix, mesh, textureId11, textureId12);
    
    //initialise cube models for the scene mapped with different textures - shaders, material, modelMatrix passed along
    mesh1 = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    gold = new Model(gl, camera, light, shader, material1, modelMatrix, mesh1, textureId9);
    post = new Model(gl, camera, light, shader, material1, modelMatrix, mesh1, textureId10);

    //scene graph start, constants and common variables
    twoBranchRoot = new NameNode("two-branch structure");
    translateX = new TransformNode("translate("+xPosition+",0,0)", Mat4Transform.translate(xPosition,0,0));
    float bodyRadius = 5.0f;
    float buttonRadius = 0.5f; 
    float headRadius = 2.5f;

    //animations
    rockto = new TransformNode("rotateAroundX("+rocktoAngle+")", Mat4Transform.rotateAroundX(rocktoAngle)); 
    rockfro = new TransformNode("rotateAroundZ("+rockfroAngle+")", Mat4Transform.rotateAroundZ(rockfroAngle));
    rollto = new TransformNode("rotateAroundX("+rolltoAngle+")", Mat4Transform.rotateAroundX(rolltoAngle));
    rollfro = new TransformNode("rotateAroundZ("+rollfroAngle+")", Mat4Transform.rotateAroundZ(rollfroAngle));
    reversetranslateToTop = new TransformNode("translate(0,"+"-"+bodyRadius+",0)", Mat4Transform.translate(0,-(bodyRadius/2),0));
    translateToTop2 = new TransformNode("translate(0,"+bodyRadius+",0)", Mat4Transform.translate(0,bodyRadius/2,0));
    slideTo = new TransformNode("translate(0,0,"+slideDistanceIncrement+")", Mat4Transform.translate(0,0,slideDistanceIncrement));
    slideFro = new TransformNode("translate(0,0,"+"-"+slideDistanceIncrement+")", Mat4Transform.translate(0,0,-slideDistanceIncrement));
    spotlightSpin = new TransformNode("rotateAroundY("+spotlightAngle+")", Mat4Transform.rotateAroundY(spotlightAngle));
    reverseSpotlightTranslate = new TransformNode("translate(-1.0f,26.0f,-1.6f)", Mat4Transform.translate(-6.0f,0,-4.0f));
    spotlightTranslateBack = new TransformNode("translate(-1.0f,26.0f,-1.6f)", Mat4Transform.translate(6.0f,0,4.0f));

    //Lower Branch
    NameNode lowerBranch = new NameNode("lower branch");
    Mat4 m = Mat4Transform.scale(bodyRadius,bodyRadius,bodyRadius);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode makeLowerBranch = new TransformNode("scale(4.0f,"+bodyRadius+",4.0f); translate(0,0.5,0)", m);
    ModelNode cube0Node = new ModelNode("Sphere(0)", sphere);

    //button01
    NameNode button01 = new NameNode("button01");
    m = Mat4Transform.scale(buttonRadius,buttonRadius,buttonRadius);
    m = Mat4.multiply(m, Mat4Transform.translate(0,bodyRadius+1.0f,bodyRadius-0.25f));
    TransformNode makeButton01 = new TransformNode("scale("+buttonRadius+","+buttonRadius+","+buttonRadius+"); translate(0,"+bodyRadius+"+1.0f,"+bodyRadius+"-0.25f)", m);
    ModelNode button01Node = new ModelNode("ButtonSphere(0)", features);

    //button02
    NameNode button02 = new NameNode("button02");
    m = Mat4Transform.scale(buttonRadius,buttonRadius,buttonRadius);
    m = Mat4.multiply(m, Mat4Transform.translate(0,bodyRadius-0.5f,bodyRadius));
    TransformNode makeButton02 = new TransformNode("scale("+buttonRadius+","+buttonRadius+","+buttonRadius+"); translate(0,"+bodyRadius+"-0.5f,"+bodyRadius+")", m);
    ModelNode button02Node = new ModelNode("ButtonSphere(1)", features);

    //button03
    NameNode button03 = new NameNode("button03");
    m = Mat4Transform.scale(buttonRadius,buttonRadius,buttonRadius);
    m = Mat4.multiply(m, Mat4Transform.translate(0,bodyRadius+2.5f,bodyRadius-0.75f));
    TransformNode makeButton03 = new TransformNode("scale("+buttonRadius+","+buttonRadius+","+buttonRadius+"); translate(0,"+bodyRadius+"+2.5f,"+bodyRadius+"-0.75f)", m);
    ModelNode button03Node = new ModelNode("ButtonSphere(1)", features);

    //Translation
    TransformNode translateToTop = new TransformNode("translate(0,"+bodyRadius+",0)",Mat4Transform.translate(0,bodyRadius,0));

    //nose
    NameNode nose = new NameNode("nose");
    m = Mat4Transform.scale(0.4f,0.4f,1.2f);
    m = Mat4.multiply(m, Mat4Transform.translate(0,headRadius+0.5f,1.0f));
    TransformNode makeNose = new TransformNode("scale(0.4f,0.4f,1.2f); translate(0,"+headRadius+"+0.5f,1.0f)",m);
    ModelNode noseNode = new ModelNode("NoseSphere(0)", features);

    //eye1
    NameNode eye01 = new NameNode("eye01");
    m = Mat4Transform.scale(0.4f,0.4f,0.4f);
    m = Mat4.multiply(m, Mat4Transform.translate(-1.0f,headRadius+1.5f,2.5f));
    TransformNode makeEye01 = new TransformNode("scale(0.4f,0.4f,0.4f); translate(-1.0f,"+headRadius+"+1.5f,2.5f)",m);
    ModelNode eyeNode01 = new ModelNode("Eye01Sphere(0)", features);

    //eye2
    NameNode eye02 = new NameNode("eye02");
    m = Mat4Transform.scale(0.4f,0.4f,0.4f);
    m = Mat4.multiply(m, Mat4Transform.translate(1.0f,headRadius+1.5f,2.5f));
    TransformNode makeEye02 = new TransformNode("scale(0.4f,0.4f,0.4f); translate(1.0f,"+headRadius+"+1.5f,2.5f)",m);
    ModelNode eyeNode02 = new ModelNode("Eye01Sphere(1)", features);

    //mouth
    NameNode mouth = new NameNode("mouth");
    m = Mat4Transform.scale(1.0f,0.5f,0.5f);
    m = Mat4.multiply(m, Mat4Transform.translate(0,headRadius-1.1f,2.0f));
    TransformNode makeMouth = new TransformNode("scale(1.0f,0.5f,0.5f); translate(0,"+headRadius+"-1.1f,2.0f)",m);
    ModelNode mouthNode = new ModelNode("MouthSphere(0)", features);

    //hatMain
    NameNode hatMain = new NameNode("hatMain");
    m = Mat4Transform.scale(2.0f,2.0f,2.0f);
    m = Mat4.multiply(m, Mat4Transform.translate(0,1.0f,0));
    TransformNode makeHatMain = new TransformNode("scale(2.0f,2.0f,2.0f); translate(0,1.0f,0)",m);
    ModelNode hatMainNode = new ModelNode("HatMainSphere(0)", hatblue);

    //hatFront
    NameNode hatFront = new NameNode("hatFront");
    m = Mat4Transform.scale(2.0f,0.5f,2.6f);
    m = Mat4.multiply(m, Mat4Transform.translate(0,4.1f,0.25f));
    TransformNode makeHatFront = new TransformNode("scale(2.0f,0.5f,2.6f); translate(0,4.1f,0.25f)",m);
    ModelNode hatFrontNode = new ModelNode("HatFrontSphere(0)", flannel);

    //hatEye01
    NameNode hatEye01 = new NameNode("hatEye01");
    m = Mat4Transform.scale(0.5f,0.5f,0.5f);
    m = Mat4.multiply(m, Mat4Transform.translate(-0.75f,5.1f,1.2f));
    TransformNode makeHatEye01 = new TransformNode("scale(0.5f,0.5f,0.5f); translate(-0.75f,5.1f,1.2f)",m);
    ModelNode hatEye01Node = new ModelNode("HatEye01Sphere(0)", hatEyes);

    //hatEye02
    NameNode hatEye02 = new NameNode("hatEye02");
    m = Mat4Transform.scale(0.5f,0.5f,0.5f);
    m = Mat4.multiply(m, Mat4Transform.translate(0.75f,5.1f,1.2f));
    TransformNode makeHatEye02 = new TransformNode("scale(0.5f,0.5f,0.5f); translate(0.75f,5.1f,1.2f)",m);
    ModelNode hatEye02Node = new ModelNode("HatEye02Sphere(0)", hatEyes);

    //hatPupil01
    NameNode hatPupil01 = new NameNode("hatPupil01");
    m = Mat4Transform.scale(0.2f,0.2f,0.2f);
    m = Mat4.multiply(m, Mat4Transform.translate(-2.0f,13.0f,4.5f));
    TransformNode makeHatPupil01 = new TransformNode("scale(0.2f,0.2f,0.2f); (-2.0f,13.0f,4.5f)",m);
    ModelNode hatPupil01Node = new ModelNode("HatPupil01Sphere(0)", pupils);

    //hatPupil02
    NameNode hatPupil02 = new NameNode("hatPupil02");
    m = Mat4Transform.scale(0.2f,0.2f,0.2f);
    m = Mat4.multiply(m, Mat4Transform.translate(2.0f,13.0f,4.5f));
    TransformNode makeHatPupil02 = new TransformNode("scale(0.2f,0.2f,0.2f); translate(2.0f,13.0f,4.5f)",m);
    ModelNode hatPupil02Node = new ModelNode("HatPupil02Sphere(1)", pupils);

    //hatEar01
    NameNode hatEar01 = new NameNode("hatEar01");
    m = Mat4Transform.scale(0.4f,2.0f,1.0f);
    m = Mat4.multiply(m, Mat4Transform.translate(-3.5f,0.8f,0));
    TransformNode makeHatEar01 = new TransformNode("scale(0.4f,2.0f,1.0f); translate(-3.5f,0.8f,0)",m);
    ModelNode hatEar01Node = new ModelNode("HatEar01Sphere(0)", flannel);

    //hatEar02
    NameNode hatEar02 = new NameNode("hatEar02");
    m = Mat4Transform.scale(0.4f,2.0f,1.0f);
    m = Mat4.multiply(m, Mat4Transform.translate(3.5f,0.8f,0));
    TransformNode makeHatEar02 = new TransformNode("scale(0.4f,2.0f,1.0f); translate(3.5f,0.8f,0)",m);
    ModelNode hatEar02Node = new ModelNode("HatEar01Sphere(1)", flannel);
  
    //hat rotation for the sides
    rotateLeftHat = new TransformNode("rotateAroundZ("+rotateLeftHatAngle+")",Mat4Transform.rotateAroundZ(rotateLeftHatAngle));
    rotateRightHat = new TransformNode("rotateAroundZ("+rotateRightHatAngle+")",Mat4Transform.rotateAroundZ(rotateRightHatAngle));

    //bauble
    NameNode bauble = new NameNode("bauble");
    m = Mat4Transform.scale(3.0f,3.0f,3.0f);
    m = Mat4.multiply(m, Mat4Transform.translate(2.0f,0.5f,-1.5f));
    TransformNode makeBauble = new TransformNode("scale(3.0f,3.0f,3.0f); translate(2.0f,0.5f,-1.5f)",m);
    ModelNode baubleNode = new ModelNode("baubleSphere(1)", baubleMap);

    //baubleTop
    NameNode baubleTop = new NameNode("baubleTop");
    m = Mat4Transform.scale(0.5f,0.5f,0.5f);
    m = Mat4.multiply(m, Mat4Transform.translate(12.0f,6.4f,-9.0f));
    TransformNode makeBaubleTop = new TransformNode("scale(0.5f,0.5f,0.5f); translate(12.0f,6.4f,-9.0f)",m);
    ModelNode baubleTopNode = new ModelNode("baubleTopCube(0)", gold);

    //baubleConnector
    NameNode baubleConnector = new NameNode("baubleConnector");
    m = Mat4Transform.scale(0.5f,0.5f,0.1f);
    m = Mat4.multiply(m, Mat4Transform.translate(12.0f,7.0f,-45.0f));
    TransformNode makeBaubleConnector = new TransformNode("scale(0.5f,0.5f,0.1f); translate(12.0f,7.0f,-45.0f)",m);
    ModelNode baubleConnectorNode = new ModelNode("baubleConnectorSphere(0)", gold1);

    //spotlightBase
    NameNode spotlightBase = new NameNode("spotlightBase");
    m = Mat4Transform.scale(0.5f,13.0f,0.5f);
    m = Mat4.multiply(m, Mat4Transform.translate(-12.0f,0.5f,-8.0f));
    TransformNode makeSpotlightBase = new TransformNode("scale(0.5f,13.0f,0.5f); translate(-12.0f,0.5f,-8.0f)",m);
    ModelNode spotlightBaseNode = new ModelNode("spotlightBaseCube(0)", post);

    //spotlightTop
    NameNode spotlightTop = new NameNode("spotlightTop");
    m = Mat4Transform.scale(5.0f,0.5f,5.0f);
    m = Mat4.multiply(m, Mat4Transform.translate(-1.2f,26.0f,-0.8f));
    TransformNode makeSpotlightTop = new TransformNode("scale(5.0f,0.5f,2.5f); translate(-1.0f,26.0f,-1.6f)",m);
    ModelNode spotlightTopNode = new ModelNode("spotlightTopSphere(0)", postTop);

    //Upper branch
    NameNode upperBranch = new NameNode("upper branch");
    m = Mat4Transform.scale(headRadius,headRadius,headRadius);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode makeUpperBranch = new TransformNode("scale("+headRadius+","+headRadius+","+headRadius+"); translate(0,0.5f,0)", m);
    ModelNode cube1Node = new ModelNode("Sphere(1)", sphere);
        
    twoBranchRoot.addChild(translateX);
      translateX.addChild(slideTo);
        slideTo.addChild(slideFro);
          slideFro.addChild(rockto);
            rockto.addChild(rockfro);
              rockfro.addChild(lowerBranch);
                lowerBranch.addChild(makeLowerBranch);
                  makeLowerBranch.addChild(cube0Node);
                lowerBranch.addChild(button01);
                  button01.addChild(makeButton01);
                    makeButton01.addChild(button01Node);
                lowerBranch.addChild(button02);
                  button02.addChild(makeButton02);
                    makeButton02.addChild(button02Node);
                lowerBranch.addChild(button03);
                  button03.addChild(makeButton03);
                    makeButton03.addChild(button03Node);
                lowerBranch.addChild(translateToTop);
                  translateToTop.addChild(reversetranslateToTop);
                    reversetranslateToTop.addChild(rollto); //reverse translation needed to change point of rotation
                      rollto.addChild(rollfro);
                        rollfro.addChild(translateToTop2);
                          translateToTop2.addChild(upperBranch);
                            upperBranch.addChild(makeUpperBranch);
                              makeUpperBranch.addChild(cube1Node);
                            upperBranch.addChild(nose);
                              nose.addChild(makeNose);
                                makeNose.addChild(noseNode);
                            upperBranch.addChild(eye01);
                              eye01.addChild(makeEye01);
                                makeEye01.addChild(eyeNode01);
                            upperBranch.addChild(eye02);
                              eye02.addChild(makeEye02);
                                makeEye02.addChild(eyeNode02);
                            upperBranch.addChild(mouth);
                              mouth.addChild(makeMouth);
                                makeMouth.addChild(mouthNode);
                            upperBranch.addChild(hatMain);
                              hatMain.addChild(makeHatMain);
                                makeHatMain.addChild(hatMainNode);
                            upperBranch.addChild(hatFront);
                              hatFront.addChild(makeHatFront);
                                makeHatFront.addChild(hatFrontNode);
                            upperBranch.addChild(hatEye01);
                              hatEye01.addChild(makeHatEye01);
                                makeHatEye01.addChild(hatEye01Node);
                            upperBranch.addChild(hatEye02);
                              hatEye02.addChild(makeHatEye02);
                                makeHatEye02.addChild(hatEye02Node);
                            upperBranch.addChild(hatPupil01);
                              hatPupil01.addChild(makeHatPupil01);
                                makeHatPupil01.addChild(hatPupil01Node);
                            upperBranch.addChild(hatPupil02);
                              hatPupil02.addChild(makeHatPupil02);
                                makeHatPupil02.addChild(hatPupil02Node);
                            upperBranch.addChild(rotateRightHat);
                              rotateRightHat.addChild(hatEar01);
                                hatEar01.addChild(makeHatEar01);
                                  makeHatEar01.addChild(hatEar01Node);
                            upperBranch.addChild(rotateLeftHat);
                              rotateLeftHat.addChild(hatEar02);
                                hatEar02.addChild(makeHatEar02);
                                  makeHatEar02.addChild(hatEar02Node);
    twoBranchRoot.addChild(bauble);
      bauble.addChild(makeBauble);
        makeBauble.addChild(baubleNode);
      bauble.addChild(baubleTop);
        baubleTop.addChild(makeBaubleTop);
          makeBaubleTop.addChild(baubleTopNode);
      bauble.addChild(baubleConnector);
        baubleConnector.addChild(makeBaubleConnector);
          makeBaubleConnector.addChild(baubleConnectorNode);
    twoBranchRoot.addChild(spotlightBase);
      spotlightBase.addChild(makeSpotlightBase);
        makeSpotlightBase.addChild(spotlightBaseNode);
      spotlightBase.addChild(reverseSpotlightTranslate);
        reverseSpotlightTranslate.addChild(spotlightSpin);//reverse translation needed to change point of rotation
          spotlightSpin.addChild(spotlightTranslateBack);
            spotlightTranslateBack.addChild(spotlightTop);
              spotlightTop.addChild(makeSpotlightTop);
                makeSpotlightTop.addChild(spotlightTopNode);
    twoBranchRoot.update();  // IMPORTANT – must be done every time any part of the scene graph changes
    //twoBranchRoot.print(0, false);
    //System.exit(0);
  }
 
  private void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    
	//render lights and set positions
	light.render(gl);
	light.setPosition(getSpotlightPosition());  // changing light position each frame
	world_light.render(gl);
	world_light.setPosition(getWorldLightPosition());
    floor.render(gl);

	//general shader
    shader.use(gl);
	shader.setVec3(gl, "world_light.position", getWorldLightPosition());
	shader.setVec3(gl, "world_light.ambient", getWorldLightAmbient());
	shader.setVec3(gl, "world_light.diffuse", getWorldLightDiffuse());
	shader.setVec3(gl, "world_light.specular", getWorldLightSpecular());
	shader.setVec3(gl, "light.ambient", getLightAmbient());
	shader.setVec3(gl, "light.diffuse", getLightDiffuse());
	shader.setVec3(gl, "light.specular", getLightSpecular());
    shader.setVec3(gl, "light.direction", getSpotlightDirection());
    shader.setFloat(gl, "light.cutOff", (float)Math.cos(Math.toRadians(cutOffValue)));
    shader.setFloat(gl, "light.outerCutOff", (float)Math.cos(Math.toRadians(17.5f)));
	
	//backwall and shader
    backwall.setModelMatrix(backwallModel());
    backwall.render(gl);
	backwall_shader.setVec3(gl, "world_light.position", getWorldLightPosition());
	backwall_shader.setVec3(gl, "world_light.ambient", getWorldLightAmbient());
	backwall_shader.setVec3(gl, "world_light.diffuse", getWorldLightDiffuse());
	backwall_shader.setVec3(gl, "world_light.specular", getWorldLightSpecular());
	backwall_shader.setVec3(gl, "light.ambient", getLightAmbient());
	backwall_shader.setVec3(gl, "light.diffuse", getLightDiffuse());
	backwall_shader.setVec3(gl, "light.specular", getLightSpecular());
    backwall_shader.setVec3(gl, "light.direction", getSpotlightDirection());
    backwall_shader.use(gl);
    backwall_shader.setFloat(gl, "offset", updateSnowX(), updateSnowY());
    backwall_shader.setFloat(gl, "light.cutOff", (float)Math.cos(Math.toRadians(cutOffValue)));
    backwall_shader.setFloat(gl, "light.outerCutOff", (float)Math.cos(Math.toRadians(17.5f)));

    updateBranches();
    twoBranchRoot.draw(gl);
  }

  //use for back wall where we texture our animating background.
  private Mat4 backwallModel() {
    float size = 16f;
    Mat4 modelMatrix = new Mat4(1);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(size,1f,size), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(0,size*0.5f,-size*0.5f), modelMatrix);
    return modelMatrix;
  }

  //update the offset of x values in the snow animation
  private float updateSnowX() {
    double elapsedTime = getSeconds()-startTime;
    double t = elapsedTime*0.1;
    float offsetX = (float)(t - Math.floor(t));
    return offsetX;
  }

  //update the offset of y values in the snow animation
  private float updateSnowY() {
    double elapsedTime = getSeconds()-startTime;
    double t = elapsedTime*0.1;
    float offsetY = (float)(t - Math.floor(t));
    return offsetY;
  }

  private void updateBranches() {
    double elapsedTime = getSeconds()-startTime;

    //snowman animations
    rockto.setTransform(Mat4Transform.rotateAroundX(rocktoAngle*(float)Math.sin(elapsedTime)));
    rockfro.setTransform(Mat4Transform.rotateAroundZ(rockfroAngle*(float)Math.sin(elapsedTime)));
    rollto.setTransform(Mat4Transform.rotateAroundX(rolltoAngle*(float)Math.sin(elapsedTime)));
    rollfro.setTransform(Mat4Transform.rotateAroundZ(rollfroAngle*(float)Math.sin(elapsedTime)));
    slideTo.setTransform(Mat4Transform.translate(0,0,slideDistanceIncrement*(float)Math.sin(elapsedTime)));
    slideFro.setTransform(Mat4Transform.translate(lateralDistanceIncrement*(float)Math.sin(elapsedTime),0,0));
    spotlightSpin.setTransform(Mat4Transform.rotateAroundY(spotlightAngle*(float)Math.sin(elapsedTime)));

    //used to neaten up the snowman's hat
    rotateLeftHat.setTransform(Mat4Transform.rotateAroundZ(rotateLeftHatAngle));
    rotateRightHat.setTransform(Mat4Transform.rotateAroundZ(rotateRightHatAngle));
    twoBranchRoot.update(); // IMPORTANT – the scene graph has changed
  }

  //Below are some methods to alter the lights' ambient, diffuse and specular values.

  private Vec3 getSpotlightPosition() {
    double elapsedTime = getSeconds()-startTime;
    float x = -2f*(float)(Math.sin(Math.toRadians(elapsedTime*50))) -6;
    float y = 12.7f;
    float z = -2f*(float)(Math.cos(Math.toRadians(elapsedTime*50))) -4;
    Vec3 m = new Vec3(x,y,z);
	return m;
  }

  private Vec3 getSpotlightDirection() {
	double elapsedTime = getSeconds()-startTime;
    float x = 1.0f*(float)Math.sin(Math.toRadians(elapsedTime*50));
    float y = -1.0f;
    float z = 0.4f + 1.0f*(float)Math.cos(Math.toRadians(elapsedTime*50));
    return new Vec3(x,y,z);
  }

  private Vec3 getWorldLightPosition() {
  	float x = 5.0f;
	float y = 14.0f;
	float z = -1.0f;
	return new Vec3(x,y,z);
  }

  public Vec3 getLightAmbient() {
  	float x = light_ambient;
	float y = light_ambient;
	float z = light_ambient;
	return new Vec3(x,y,z);
  }

  public Vec3 getLightDiffuse() {
  	float x = light_diffuse;
	float y = light_diffuse;
	float z = light_diffuse;
	return new Vec3(x,y,z);
  }

  public Vec3 getLightSpecular() {
  	float x = light_spec;
	float y = light_spec;
	float z = light_spec;
	return new Vec3(x,y,z);
  }

  private Vec3 getWorldLightAmbient() {
  	float x = world_light_ambient;
	float y = world_light_ambient;
	float z = world_light_ambient;
	return new Vec3(x,y,z);
  }

  private Vec3 getWorldLightDiffuse() {
  	float x = world_light_diffuse;
	float y = world_light_diffuse;
	float z = world_light_diffuse;
	return new Vec3(x,y,z);
  }

  private Vec3 getWorldLightSpecular() {
  	float x = world_light_spec;
	float y = world_light_spec;
	float z = world_light_spec;
	return new Vec3(x,y,z);
  }

  private float getCutOff() {
  	float c = cutOffValue;
	return c;
  }

  // ***************************************************
  /* TIME
   */ 
  
  private double startTime;
  
  private double getSeconds() {
    return System.currentTimeMillis()/1000.0;
  }

  // ***************************************************
  
}