import numpy as np
import matplotlib.pyplot as plt
from matplotlib.widgets import Slider

init_c_real = 0.36
init_c_imag = 0.36
init_scale = 1.2
init_max_iter = 120
resolution = 400

def compute_julia(c, scale_y, max_iter, res):
    scale_x = scale_y * (1.0 / 1.2)
    x = np.linspace(-scale_x, scale_x, res)
    y = np.linspace(-scale_y, scale_y, res)
    X, Y = np.meshgrid(x, y)
    Z = X - 1j * Y

    counts = np.zeros(Z.shape, dtype=int)
    mask = np.ones(Z.shape, dtype=bool)

    for i in range(int(max_iter)):
        escaped = np.abs(Z) > 2
        counts[mask & escaped] = i
        mask &= ~escaped
        if not np.any(mask):
            break
        Z[mask] = Z[mask]**2 + c

    counts[mask] = max_iter
    return counts, [-scale_x, scale_x, -scale_y, scale_y]

fig, ax = plt.subplots(1, 1, figsize=(8, 7))
plt.subplots_adjust(bottom=0.28)

data, extent = compute_julia(complex(init_c_real, init_c_imag), init_scale, init_max_iter, resolution)
img = ax.imshow(data, extent=extent, cmap='hsv', origin='lower')
title = ax.set_title(f"C = {init_c_real:.2f} + {init_c_imag:.2f}i")

ax_cr = plt.axes([0.2, 0.18, 0.65, 0.03])
ax_ci = plt.axes([0.2, 0.13, 0.65, 0.03])
ax_scale = plt.axes([0.2, 0.08, 0.65, 0.03])
ax_iter = plt.axes([0.2, 0.03, 0.65, 0.03])

s_cr = Slider(ax_cr, 'Re(C)', -1.5, 1.5, valinit=init_c_real)
s_ci = Slider(ax_ci, 'Im(C)', -1.5, 1.5, valinit=init_c_imag)
s_scale = Slider(ax_scale, 'Масштаб Y', 0.1, 2.5, valinit=init_scale)
s_iter = Slider(ax_iter, 'Итерации', 20, 300, valinit=init_max_iter, valstep=10)

def update(val):
    c = complex(s_cr.val, s_ci.val)
    scale_y = s_scale.val
    iters = int(s_iter.val)
    
    new_data, new_extent = compute_julia(c, scale_y, iters, resolution)
    img.set_data(new_data)
    img.set_extent(new_extent)
    img.set_clim(vmin=0, vmax=iters)
    title.set_text(f"C = {c.real:.2f} + {c.imag:.2f}i")
    fig.canvas.draw_idle()

s_cr.on_changed(update)
s_ci.on_changed(update)
s_scale.on_changed(update)
s_iter.on_changed(update)

plt.show()