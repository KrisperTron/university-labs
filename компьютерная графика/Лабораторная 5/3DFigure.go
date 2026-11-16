package main

import (
	"fmt"
	"image/color"
	"log"
	"math"

	"github.com/hajimehoshi/ebiten/v2"
	"github.com/hajimehoshi/ebiten/v2/ebitenutil"
	"github.com/hajimehoshi/ebiten/v2/vector"
)

type Point3D struct {
	X, Y, Z float64
}

type Game struct {
	grid       [][]Point3D // Сетка точек цилиндра
	tx, ty, tz float64     // Перемещение по осям X, Y, Z
	scale      float64     // Масштабирование
	rx, ry, rz float64     // Углы вращения вокруг осей X, Y, Z
}

func NewGame() *Game {
	g := &Game{
		scale: 1.0,
		rx:    1.1,
		rz:    0.3,
	}

	// Параметры цилиндра по формулам:
	// x = R*sin(a), y = R*cos(a), z = H*k
	R, H := 100.0, 240.0
	stepsK, stepsA := 12, 24

	for i := 0; i <= stepsK; i++ {
		k := -0.5 + float64(i)/float64(stepsK) // k от -1/2 до 1/2
		var row []Point3D
		for j := 0; j < stepsA; j++ {
			a := 2 * math.Pi * float64(j) / float64(stepsA) // alpha от 0 до 2*Pi
			row = append(row, Point3D{
				X: R * math.Sin(a),
				Y: R * math.Cos(a),
				Z: H * k,
			})
		}
		g.grid = append(g.grid, row)
	}
	return g
}

// Применение 3 базовых аффинных преобразований к точке
func (g *Game) transform(p Point3D) Point3D {
	// 1. Масштабирование
	x, y, z := p.X*g.scale, p.Y*g.scale, p.Z*g.scale

	// 2. Вращение вокруг оси X
	y, z = y*math.Cos(g.rx)-z*math.Sin(g.rx), y*math.Sin(g.rx)+z*math.Cos(g.rx)
	// оси Y
	x, z = x*math.Cos(g.ry)+z*math.Sin(g.ry), -x*math.Sin(g.ry)+z*math.Cos(g.ry)
	// оси Z
	x, y = x*math.Cos(g.rz)-y*math.Sin(g.rz), x*math.Sin(g.rz)+y*math.Cos(g.rz)

	// 3. Перемещение вдоль всех осей
	return Point3D{x + g.tx, y + g.ty, z + g.tz}
}

// Функция вывода полигона
func drawPolygon(screen *ebiten.Image, pts ...Point3D) {
	cx, cy := float32(400), float32(300) // Центр экрана
	col := color.RGBA{30, 30, 30, 255}

	for i := 0; i < len(pts); i++ {
		p1 := pts[i]
		p2 := pts[(i+1)%len(pts)]
		// Ортогональная проекция на плоскость Z=0
		vector.StrokeLine(screen, cx+float32(p1.X), cy+float32(p1.Y), cx+float32(p2.X), cy+float32(p2.Y), 1, col, true)
	}
}

func (g *Game) Update() error {
	// Перемещение (W/S/A/D - X И Y; Q/E — перемещение по Z)
	if ebiten.IsKeyPressed(ebiten.KeyA) {
		g.tx -= 4 // влево
	}
	if ebiten.IsKeyPressed(ebiten.KeyD) {
		g.tx += 4 // вправо
	}
	if ebiten.IsKeyPressed(ebiten.KeyW) {
		g.ty -= 4 // вверх
	}
	if ebiten.IsKeyPressed(ebiten.KeyS) {
		g.ty += 4 // вниз
	}
	if ebiten.IsKeyPressed(ebiten.KeyQ) {
		g.tz -= 4 // назад по оси Z
	}
	if ebiten.IsKeyPressed(ebiten.KeyE) {
		g.tz += 4 // вперед по оси Z
	}

	// Вращение (Стрелки и U/O)
	if ebiten.IsKeyPressed(ebiten.KeyUp) {
		g.rx -= 0.03
	}
	if ebiten.IsKeyPressed(ebiten.KeyDown) {
		g.rx += 0.03
	}
	if ebiten.IsKeyPressed(ebiten.KeyLeft) {
		g.ry -= 0.03
	}
	if ebiten.IsKeyPressed(ebiten.KeyRight) {
		g.ry += 0.03
	}
	if ebiten.IsKeyPressed(ebiten.KeyU) {
		g.rz -= 0.03
	}
	if ebiten.IsKeyPressed(ebiten.KeyO) {
		g.rz += 0.03
	}

	// Масштабирование (+/-)
	if ebiten.IsKeyPressed(ebiten.KeyEqual) {
		g.scale += 0.02
	}
	if ebiten.IsKeyPressed(ebiten.KeyMinus) && g.scale > 0.1 {
		g.scale -= 0.02
	}

	return nil
}

func (g *Game) Draw(screen *ebiten.Image) {
	screen.Fill(color.White)

	rows := len(g.grid)
	cols := len(g.grid[0])

	// Проходим по сетке и выводим каркас из четырехугольных полигонов и узловых точек
	for i := 0; i < rows-1; i++ {
		for j := 0; j < cols; j++ {
			nextJ := (j + 1) % cols

			p0 := g.transform(g.grid[i][j])
			p1 := g.transform(g.grid[i][nextJ])
			p2 := g.transform(g.grid[i+1][nextJ])
			p3 := g.transform(g.grid[i+1][j])

			// Вывод полигона и прямых линий
			drawPolygon(screen, p0, p1, p2, p3)

			vector.DrawFilledCircle(screen, 400+float32(p0.X), 300+float32(p0.Y), 1.5, color.RGBA{200, 0, 0, 255}, true)
			if i == rows-2 {
				vector.DrawFilledCircle(screen, 400+float32(p3.X), 300+float32(p3.Y), 1.5, color.RGBA{200, 0, 0, 255}, true)
			}
		}
	}

	vector.DrawFilledRect(screen, 0, 0, 800, 28, color.RGBA{30, 30, 30, 255}, false)
	// Перемещение: WASDEQ | Вращение: Стрелки, U/O | Масштаб: +/-
	ebitenutil.DebugPrintAt(screen, fmt.Sprintf("Move WASD(XY)/QE(Z) | T: (%.0f, %.0f, %.0f)", g.tx, g.ty, g.tz)+" | Rotate: Arrows, U/O | Scale: +/-", 10, 6)
}

func (g *Game) Layout(w, h int) (int, int) { return 800, 600 }

func main() {
	ebiten.SetWindowSize(800, 600)
	ebiten.SetWindowTitle("6201-090301D, Бояков Алексей")
	if err := ebiten.RunGame(NewGame()); err != nil {
		log.Fatal(err)
	}
}
