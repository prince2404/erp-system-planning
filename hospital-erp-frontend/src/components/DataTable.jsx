export default function DataTable({ columns, rows, empty = 'No records found' }) {
  const data = Array.isArray(rows) ? rows : rows?.content || (rows && typeof rows === 'object' ? [rows] : []);

  if (!data.length) {
    return (
      <div className="rounded-2xl border border-dashed border-slate-300 bg-slate-50 p-8 text-center">
        <p className="text-sm font-medium text-slate-700">{empty}</p>
        <p className="mt-1 text-xs text-slate-500">Create or import records to see them here.</p>
      </div>
    );
  }

  return (
    <div className="overflow-hidden rounded-2xl border border-slate-200">
      <div className="overflow-x-auto">
        <table className="min-w-full divide-y divide-slate-200 text-sm">
          <thead className="bg-slate-50 text-left text-xs uppercase tracking-wide text-slate-500">
            <tr>
              {columns.map((column) => (
                <th key={column.key} className="px-4 py-3 font-semibold">
                  {column.label}
                </th>
              ))}
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100 bg-white">
            {data.map((row, index) => (
              <tr key={row.id ?? row.uhid ?? index} className={index % 2 ? 'bg-slate-50/60' : 'bg-white'}>
                {columns.map((column) => (
                  <td key={column.key} className="whitespace-nowrap px-4 py-3 text-slate-700">
                    {column.render ? column.render(row) : valueAt(row, column.key)}
                  </td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

function valueAt(row, key) {
  const value = key.split('.').reduce((acc, part) => acc?.[part], row);
  if (value === null || value === undefined || value === '') {
    return '-';
  }
  if (typeof value === 'boolean') {
    return value ? 'Yes' : 'No';
  }
  return String(value);
}
