import tkinter as tk

WIDTH, HEIGHT = 400, 400

window = tk.Tk()
window.title("Лабораторная работа 3: Яблоко")
canvas = tk.Canvas(window, width=WIDTH, height=HEIGHT, bg="white")
canvas.pack()

img = tk.PhotoImage(width=WIDTH, height=HEIGHT)
canvas.create_image((WIDTH//2, HEIGHT//2), image=img, state="normal")

pixels = [["#ffffff" for _ in range(WIDTH)] for _ in range(HEIGHT)]

def get_pixel(x, y):
    if 0 <= x < WIDTH and 0 <= y < HEIGHT:
        return pixels[y][x]
    return None

def set_pixel(x, y, color):
    if 0 <= x < WIDTH and 0 <= y < HEIGHT:
        pixels[y][x] = color
        img.put(color, (x, y))

def draw_line(x0, y0, x1, y1, color):
    dx = abs(x1 - x0)
    dy = abs(y1 - y0)
    sx = 1 if x0 < x1 else -1
    sy = 1 if y0 < y1 else -1
    err = dx - dy
    
    while True:
        set_pixel(x0, y0, color)
        if x0 == x1 and y0 == y1:
            break
        e2 = 2 * err
        if e2 > -dy:
            err -= dy
            x0 += sx
        if e2 < dx:
            err += dx
            y0 += sy

def draw_circle(xc, yc, r, color):
    x = 0
    y = r
    d = 3 - 2 * r
    
    def draw_sym(xc, yc, x, y):
        set_pixel(xc+x, yc+y, color); set_pixel(xc-x, yc+y, color)
        set_pixel(xc+x, yc-y, color); set_pixel(xc-x, yc-y, color)
        set_pixel(xc+y, yc+x, color); set_pixel(xc-y, yc+x, color)
        set_pixel(xc+y, yc-x, color); set_pixel(xc-y, yc-x, color)
        
    while y >= x:
        draw_sym(xc, yc, x, y)
        if d < 0:
            d = d + 4 * x + 6
        else:
            d = d + 4 * (x - y) + 10
            y -= 1
        x += 1

def draw_bezier(x0, y0, x1, y1, x2, y2, color):
    steps = 1000
    for i in range(steps + 1):
        t = i / steps
        x = int((1-t)**2 * x0 + 2*(1-t)*t * x1 + t**2 * x2)
        y = int((1-t)**2 * y0 + 2*(1-t)*t * y1 + t**2 * y2)
        set_pixel(x, y, color)

def flood_fill(x, y, fill_color):
    target_color = get_pixel(x, y)
    if target_color == fill_color or target_color is None:
        return

    stack = [(x, y)]
    while stack:
        cx, cy = stack.pop()
        if get_pixel(cx, cy) == target_color:
            set_pixel(cx, cy, fill_color)
            stack.append((cx + 1, cy))
            stack.append((cx - 1, cy))
            stack.append((cx, cy + 1))
            stack.append((cx, cy - 1))

draw_circle(200, 220, 80, "#000000") 
flood_fill(200, 220, "#ff4444") 

draw_line(200, 140, 220, 90, "#8b4513")
draw_line(201, 140, 221, 90, "#8b4513")

draw_bezier(210, 110, 260, 80, 270, 130, "#000000")
draw_bezier(210, 110, 230, 150, 270, 130, "#000000")
flood_fill(240, 120, "#32cd32")

window.mainloop()
