final Light light1 = new PerezLight();

final Material material1 = new MatteMaterial(new CheckerboardTexture());
final Material material2 = new BullseyeMaterial(new MatteMaterial(new Color3F(1.0F, 0.01F, 0.01F)), new MatteMaterial(new Color3F(0.01F, 1.0F, 0.01F)), new Point3F(10.0F, 10.0F, 10.0F));

final Shape3F shape1 = new Plane3F();
final Shape3F shape2 = new Sphere3F();

final Transform transform1 = new Transform(new Point3F(0.0F, 0.0F, 0.0F), Quaternion4F.from(Matrix44F.rotateX(AngleF.degrees(90.0F))));
final Transform transform2 = new Transform(new Point3F(0.0F, 1.0F, 0.0F), Quaternion4F.from(Matrix44F.rotateX(AngleF.degrees(270.0F))));

final Primitive primitive1 = new Primitive(material1, shape1, transform1);
final Primitive primitive2 = new Primitive(material2, shape2, transform2);

final Camera camera = new Camera(new Point3F(0.0F, 2.0F, -10.0F), AngleF.degrees(40.0F));

scene.addLight(light1);
scene.addPrimitive(primitive1);
scene.addPrimitive(primitive2);
scene.setCamera(camera);
scene.setName("BullseyeMaterial");