export default function sketch(p:any){
    let canvas:any;

    p.setup = () => {
        canvas = p.createCanvas(710, 400, p.WEBGL);
        p.noStroke();
    }

    p.draw = () => {
        p.background(250);

        p.translate(-240, -100, 0);
        p.normalMaterial();
        p.push();
        p.rotateZ(p.frameCount * 0.01);
        p.rotateX(p.frameCount * 0.01);
        p.rotateY(p.frameCount * 0.01);
        p.plane(70);
        p.pop();

        p.translate(240, 0, 0);
        p.push();
        p.rotateZ(p.frameCount * 0.01);
        p.rotateX(p.frameCount * 0.01);
        p.rotateY(p.frameCount * 0.01);
        p.box(70, 70, 70);
        p.pop();

        p.translate(240, 0, 0);
        p.push();
        p.rotateZ(p.frameCount * 0.01);
        p.rotateX(p.frameCount * 0.01);
        p.rotateY(p.frameCount * 0.01);
        p.cylinder(70, 70);
        p.pop();

        p.translate(-240 * 2, 200, 0);
        p.push();
        p.rotateZ(p.frameCount * 0.01);
        p.rotateX(p.frameCount * 0.01);
        p.rotateY(p.frameCount * 0.01);
        p.cone(70, 70);
        p.pop();

        p.translate(240, 0, 0);
        p.push();
        p.rotateZ(p.frameCount * 0.01);
        p.rotateX(p.frameCount * 0.01);
        p.rotateY(p.frameCount * 0.01);
        p.torus(70, 20);
        p.pop();

        p.translate(240, 0, 0);
        p.push();
        p.rotateZ(p.frameCount * 0.01);
        p.rotateX(p.frameCount * 0.01);
        p.rotateY(p.frameCount * 0.01);
        p.sphere(70);
        p.pop();
    }

    p.myCustomRedrawAccordingToNewPropsHandler = (newProps:any) => {
        if(canvas) //Make sure the canvas has been created
            p.fill(newProps.color);
    }
}