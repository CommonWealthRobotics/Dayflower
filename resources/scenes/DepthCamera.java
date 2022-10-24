Texture texture = new FunctionTexture(intersection -> {
	Point3F eye = intersection.getRay().getOrigin();
	Point3F lookAt = intersection.getSurfaceIntersectionPoint();
	
	float distanceSquared = Point3F.distanceSquared(eye, lookAt);
	float scale = 0.5F;
	float intensity = 1.0F / (distanceSquared * scale);
	
	return Color3F.maxTo1(Color3F.minTo0(new Color3F(intensity)));
});

Material material = new MatteMaterial(texture);

Shape3F shapePlane = new Plane3F();
Shape3F shapeSphere = new Sphere3F();

Texture textureCheckerboardGray = new CheckerboardTexture();
Texture textureCheckerboardRed = new CheckerboardTexture(new Color3F(1.0F, 0.01F, 0.01F), Color3F.WHITE, AngleF.degrees(0.0F), new Vector2F(4.0F, 4.0F));

Material materialPlane = new MatteMaterial(new CheckerboardTexture());
Material materialSphereA = new ClearCoatMaterial(new Color3F(1.0F, 0.01F, 0.01F));
Material materialSphereB = new DisneyMaterial(new Color3F(255, 127, 80), Color3F.BLACK, Color3F.BLACK, 0.4F, 1.75F, 1.0F, 1.0F, 1.2F, 0.0F, 0.39F, 0.475F, 0.1F, 0.5F, 0.5F, 0.0F);
Material materialSphereC = new GlassMaterial(Color3F.WHITE, new Color3F(1.0F, 1.0F, 0.5F));
Material materialSphereD = new GlossyMaterial(Color3F.GRAY, Color3F.BLACK, 0.2F);
Material materialSphereE = new MatteMaterial(new Color3F(0.1F, 1.0F, 0.1F));
Material materialSphereF = new MetalMaterial();
Material materialSphereG = new MirrorMaterial(Color3F.GRAY);
Material materialSphereH = new PlasticMaterial();
Material materialSphereI = new SubstrateMaterial(new Color3F(1.0F, 0.2F, 0.2F));


Shape3F shapeSphereA = new Sphere3F();
Shape3F shapeSphereB = new Sphere3F();
Shape3F shapeSphereC = new Sphere3F();
Shape3F shapeSphereD = new Sphere3F();
Shape3F shapeSphereE = new Sphere3F();
Shape3F shapeSphereF = new Sphere3F();
Shape3F shapeSphereG = new Sphere3F();
Shape3F shapeSphereH = new Sphere3F();
Shape3F shapeSphereI = new Sphere3F();

Transform transformSphereA = new Transform(new Point3F(- 7.5F,  1.00F, 5.0F), Quaternion4F.from(Matrix44F.rotateX(AngleF.degrees(270.0F))));
Transform transformSphereB = new Transform(new Point3F(- 5.0F,  1.00F, 8.0F), Quaternion4F.from(Matrix44F.rotateX(AngleF.degrees(270.0F))));
Transform transformSphereC = new Transform(new Point3F(- 2.5F,  2.00F, 2.0F), Quaternion4F.from(Matrix44F.rotateX(AngleF.degrees(270.0F))));
Transform transformSphereD = new Transform(new Point3F(+ 0.0F,  1.00F, 5.0F), Quaternion4F.from(Matrix44F.rotateX(AngleF.degrees(270.0F))));
Transform transformSphereE = new Transform(new Point3F(+ 2.5F,  1.00F, 5.0F), Quaternion4F.from(Matrix44F.rotateX(AngleF.degrees(270.0F))));
Transform transformSphereF = new Transform(new Point3F(+ 5.0F,  1.00F, 5.0F), Quaternion4F.from(Matrix44F.rotateX(AngleF.degrees(270.0F))));
Transform transformSphereG = new Transform(new Point3F(+ 7.5F,  1.00F, 5.0F), Quaternion4F.from(Matrix44F.rotateX(AngleF.degrees(270.0F))));
Transform transformSphereH = new Transform(new Point3F(+10.0F,  1.00F, 5.0F), Quaternion4F.from(Matrix44F.rotateX(AngleF.degrees(270.0F))));
Transform transformSphereI = new Transform(new Point3F(+12.5F,  1.00F, 5.0F), Quaternion4F.from(Matrix44F.rotateX(AngleF.degrees(270.0F))));

Transform transformPlane = new Transform(new Point3F(0.0F, 0.0F, 0.0F), Quaternion4F.from(Matrix44F.rotateX(AngleF.degrees(90.0F))));
Transform transformSphere = new Transform(new Point3F(- 7.5F,  1.00F, 5.0F), Quaternion4F.from(Matrix44F.rotateX(AngleF.degrees(270.0F))));

Camera camera = new Camera(new Point3F(0.0F, 1.0F, -5.0F));
camera.setResolution(2560.0F / 3.0F, 1440.0F / 3.0F);
camera.setFieldOfViewX(AngleF.degrees(40.0F));
camera.setFieldOfViewY();
camera.setSamplingCenter(true);

ImageF imageF = new IntImageF();
imageF.clear(Color3F.WHITE);

scene.addLight(new ImageLight(imageF));
scene.addPrimitive(new Primitive(material, shapePlane, transformPlane));
scene.addPrimitive(new Primitive(material, shapeSphere, transformSphere));
scene.addPrimitive(new Primitive(materialSphereA, shapeSphereA, transformSphereA));
scene.addPrimitive(new Primitive(materialSphereB, shapeSphereB, transformSphereB));
scene.addPrimitive(new Primitive(materialSphereC, shapeSphereC, transformSphereC));
scene.addPrimitive(new Primitive(materialSphereD, shapeSphereD, transformSphereD));
scene.addPrimitive(new Primitive(materialSphereE, shapeSphereE, transformSphereE));
scene.addPrimitive(new Primitive(materialSphereF, shapeSphereF, transformSphereF));
scene.addPrimitive(new Primitive(materialSphereG, shapeSphereG, transformSphereG));
scene.addPrimitive(new Primitive(materialSphereH, shapeSphereH, transformSphereH));
scene.addPrimitive(new Primitive(materialSphereI, shapeSphereI, transformSphereI));
scene.setCamera(camera);
scene.setName("DepthCamera");