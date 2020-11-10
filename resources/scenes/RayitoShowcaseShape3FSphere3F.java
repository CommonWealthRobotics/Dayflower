Material material = new LambertianMaterial();

Shape3F shape = new Sphere3F(10.0F);

Texture texture1 = new ConstantTexture(Color3F.GRAY);
Texture texture2 = new ConstantTexture();

Matrix44F matrix = Matrix44F.translate(0.0F, 2.0F, 20.0F);

scene.addLight(new PerezLight());
scene.addPrimitive(new Primitive(material, shape, texture1, texture2, matrix));
scene.setCamera(new Camera(new Point3F(0.0F, 2.0F, 0.0F)));
scene.setName("RayitoShowcaseShape3FSphere3F");